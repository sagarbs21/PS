from datetime import datetime
from pydantic import BaseModel, ConfigDict


class PaymentCreate(BaseModel):
    booking_id: str
    amount_inr: int
    method: str


class PaymentOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: str
    booking_id: str
    amount_inr: int
    status: str
    provider: str
    txn_id: str | None
    created_at: datetime
