import logging
import secrets
from datetime import datetime, timedelta, timezone

import httpx
from sqlalchemy.orm import Session

from app.core.config import settings
from app.core.security import hash_otp
from app.models.otp import OtpCode

logger = logging.getLogger(__name__)

RESEND_ENDPOINT = "https://api.resend.com/emails"


def generate_code() -> str:
    return f"{secrets.randbelow(1_000_000):06d}"


def send_otp_email(email: str, code: str) -> None:
    """Send the OTP via the Resend HTTP API (works on hosts that block SMTP)."""
    if not settings.resend_api_key:
        raise RuntimeError("RESEND_API_KEY is not configured")

    response = httpx.post(
        RESEND_ENDPOINT,
        headers={"Authorization": f"Bearer {settings.resend_api_key}"},
        json={
            "from": settings.email_from,
            "to": [email],
            "subject": "Your PoojaSeva verification code",
            "text": (
                f"Your PoojaSeva OTP is {code}. "
                f"It expires in {settings.otp_expires_minutes} minutes."
            ),
        },
        timeout=15.0,
    )
    if response.status_code >= 400:
        logger.error("Resend API error %s: %s", response.status_code, response.text)
        response.raise_for_status()


def create_otp(db: Session, phone: str, email: str | None = None) -> str:
    code = generate_code()
    expires_at = datetime.now(timezone.utc) + timedelta(minutes=settings.otp_expires_minutes)
    otp = OtpCode(phone=phone, code_hash=hash_otp(code), expires_at=expires_at)
    db.add(otp)
    db.commit()

    if settings.otp_mode == "stub":
        return code

    if not email:
        raise ValueError("Email is required to receive the OTP")
    send_otp_email(email, code)
    return ""
