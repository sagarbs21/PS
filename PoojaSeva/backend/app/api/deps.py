from fastapi import Depends, HTTPException
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from jose import JWTError
from sqlalchemy.orm import Session

from app.core.database import SessionLocal
from app.core.security import decode_access_token
from app.models.revoked_token import RevokedToken
from app.models.user import User


def get_db() -> Session:
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


security = HTTPBearer()


def get_token_payload(
    credentials: HTTPAuthorizationCredentials = Depends(security),
) -> dict:
    token = credentials.credentials
    try:
        payload = decode_access_token(token)
    except JWTError:
        raise HTTPException(status_code=401, detail="Invalid token")
    return payload


def get_current_user(
    payload: dict = Depends(get_token_payload),
    db: Session = Depends(get_db),
) -> User:

    user_id = payload.get("sub")
    if user_id is None:
        raise HTTPException(status_code=401, detail="Invalid token")

    try:
        user_id_int = int(user_id)
    except ValueError:
        raise HTTPException(status_code=401, detail="Invalid token")

    user = db.query(User).filter(User.id == user_id_int).first()
    if user is None:
        raise HTTPException(status_code=401, detail="User not found")

    jti = payload.get("jti")
    if jti and db.query(RevokedToken).filter(RevokedToken.jti == jti).first():
        raise HTTPException(status_code=401, detail="Token revoked")
    return user


def require_role(role: str):
    def checker(user: User = Depends(get_current_user)) -> User:
        if user.role != role:
            raise HTTPException(status_code=403, detail="Forbidden")
        return user

    return checker
