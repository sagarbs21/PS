from datetime import datetime, timedelta
import random
import smtplib
from email.message import EmailMessage

from sqlalchemy.orm import Session
from twilio.rest import Client

from app.core.config import settings
from app.core.security import hash_otp
from app.models.otp import OtpCode


def generate_code() -> str:
    return f"{random.randint(0, 999999):06d}"


def send_otp_sms(phone: str, code: str) -> None:
    client = Client(settings.twilio_account_sid, settings.twilio_auth_token)
    body = f"Your PoojaSeva OTP is {code}. It expires in {settings.otp_expires_minutes} minutes."
    client.messages.create(
        to=phone,
        from_=settings.twilio_from_number,
        body=body,
    )


def send_otp_email(email: str, code: str) -> None:
    msg = EmailMessage()
    msg["Subject"] = "Your PoojaSeva OTP"
    msg["From"] = settings.smtp_from
    msg["To"] = email
    msg.set_content(
        f"Your PoojaSeva OTP is {code}. It expires in {settings.otp_expires_minutes} minutes."
    )

    with smtplib.SMTP(settings.smtp_host, settings.smtp_port, timeout=15) as server:
        server.starttls()
        server.login(settings.smtp_user, settings.smtp_password)
        server.send_message(msg)


def create_otp(db: Session, phone: str, email: str | None = None) -> str:
    code = generate_code()
    expires_at = datetime.utcnow() + timedelta(minutes=settings.otp_expires_minutes)
    otp = OtpCode(phone=phone, code_hash=hash_otp(code), expires_at=expires_at)
    db.add(otp)
    db.commit()

    if settings.otp_mode == "stub":
        return code
    if settings.otp_mode == "email":
        if not email:
            raise ValueError("Email is required for email OTP mode")
        send_otp_email(email, code)
        return ""

    send_otp_sms(phone, code)
    return ""
