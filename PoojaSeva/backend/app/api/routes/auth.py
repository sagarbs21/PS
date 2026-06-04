import logging
from datetime import datetime, timedelta, timezone

from fastapi import APIRouter, Depends, HTTPException
from fastapi.security import HTTPAuthorizationCredentials
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from app.api.deps import get_current_user, get_db, get_token_payload, security
from app.core.config import settings
from app.core.security import create_access_token, hash_otp
from app.models.otp import OtpCode
from app.models.revoked_token import RevokedToken
from app.models.user import User
from app.schemas.auth import OtpRequest, OtpVerify, TokenResponse, UserOut
from app.services.otp_service import create_otp

router = APIRouter()
logger = logging.getLogger(__name__)


def _as_utc(value: datetime) -> datetime:
    """Coerce a possibly-naive datetime (older rows / SQLite) to aware UTC."""
    if value.tzinfo is None:
        return value.replace(tzinfo=timezone.utc)
    return value


@router.post("/otp/request")
def request_otp(payload: OtpRequest, db: Session = Depends(get_db)) -> dict:
    try:
        code = create_otp(db, payload.phone, payload.email)
    except ValueError as exc:
        raise HTTPException(status_code=400, detail=str(exc))
    except Exception:
        logger.exception("Failed to deliver OTP (mode=%s)", settings.otp_mode)
        raise HTTPException(
            status_code=502,
            detail="Could not send the OTP. The server's email/SMS settings are missing or invalid.",
        )
    if settings.otp_mode == "stub":
        return {"status": "stub", "code": code}
    return {"status": "sent"}


@router.post("/otp/verify", response_model=TokenResponse)
def verify_otp(payload: OtpVerify, db: Session = Depends(get_db)) -> TokenResponse:
    record = (
        db.query(OtpCode)
        .filter(OtpCode.phone == payload.phone)
        .order_by(OtpCode.created_at.desc())
        .first()
    )
    if record is None:
        raise HTTPException(status_code=400, detail="Request an OTP first")
    if _as_utc(record.expires_at) < datetime.now(timezone.utc):
        raise HTTPException(status_code=400, detail="OTP expired. Request a new one")
    if record.attempts >= settings.otp_max_attempts:
        raise HTTPException(status_code=429, detail="Too many attempts. Request a new OTP")
    if record.code_hash != hash_otp(payload.code):
        record.attempts += 1
        db.commit()
        raise HTTPException(status_code=400, detail="Invalid OTP")

    # OTP is correct: consume every code for this phone so it cannot be reused.
    db.query(OtpCode).filter(OtpCode.phone == payload.phone).delete()
    db.commit()

    user = db.query(User).filter(User.phone == payload.phone).first()
    if user is None:
        role = "admin" if payload.phone in settings.admin_phone_set() else "user"
        user = User(phone=payload.phone, role=role)
        db.add(user)
        try:
            db.commit()
            db.refresh(user)
        except IntegrityError:
            # Concurrent first-time verify for the same phone created the row first.
            db.rollback()
            user = db.query(User).filter(User.phone == payload.phone).first()
    elif payload.phone in settings.admin_phone_set() and user.role != "admin":
        user.role = "admin"
        db.commit()

    token = create_access_token(str(user.id), {"phone": user.phone, "role": user.role})
    expires_at = datetime.now(timezone.utc) + timedelta(minutes=settings.jwt_access_token_expires_minutes)
    return TokenResponse(
        access_token=token,
        expires_at=expires_at,
        user=UserOut(id=user.id, phone=user.phone, role=user.role),
    )


@router.get("/me", response_model=UserOut)
def me(current_user: User = Depends(get_current_user)) -> UserOut:
    return UserOut(id=current_user.id, phone=current_user.phone, role=current_user.role)


@router.post("/logout")
def logout(
    credentials: HTTPAuthorizationCredentials = Depends(security),
    payload: dict = Depends(get_token_payload),
    db: Session = Depends(get_db),
) -> dict:
    jti = payload.get("jti")
    exp = payload.get("exp")
    if jti and exp:
        already_revoked = db.query(RevokedToken).filter(RevokedToken.jti == jti).first()
        if already_revoked is None:
            revoked = RevokedToken(
                jti=jti, expires_at=datetime.fromtimestamp(exp, tz=timezone.utc)
            )
            db.add(revoked)
            try:
                db.commit()
            except IntegrityError:
                db.rollback()
    return {"status": "logged_out"}
