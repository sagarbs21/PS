from datetime import datetime
from pydantic import BaseModel


class PaymentCreate(BaseModel):
    booking_id: str
    amount_inr: int
    method: str


class PaymentOut(BaseModel):
    id: str
    booking_id: str
    amount_inr: int
    status: str
    provider: str
    txn_id: str | None
    created_at: datetime
