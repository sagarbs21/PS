from datetime import datetime
from pydantic import BaseModel


class OtpRequest(BaseModel):
    phone: str
    email: str | None = None


class OtpVerify(BaseModel):
    phone: str
    code: str


class UserOut(BaseModel):
    id: int
    phone: str
    role: str


class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    expires_at: datetime
    user: UserOut
