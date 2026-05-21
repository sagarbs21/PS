import hashlib
import uuid
from datetime import datetime, timedelta
from typing import Any

from jose import JWTError, jwt

from app.core.config import settings


def hash_otp(code: str) -> str:
    raw = f"{settings.jwt_secret_key}:{code}".encode("utf-8")
    return hashlib.sha256(raw).hexdigest()


def create_access_token(subject: str, extra: dict[str, Any] | None = None) -> str:
    now = datetime.utcnow()
    expire = now + timedelta(minutes=settings.jwt_access_token_expires_minutes)
    payload: dict[str, Any] = {"sub": subject, "iat": now, "exp": expire, "jti": uuid.uuid4().hex}
    if extra:
        payload.update(extra)
    return jwt.encode(payload, settings.jwt_secret_key, algorithm=settings.jwt_algorithm)


def decode_access_token(token: str) -> dict[str, Any]:
    return jwt.decode(token, settings.jwt_secret_key, algorithms=[settings.jwt_algorithm])
