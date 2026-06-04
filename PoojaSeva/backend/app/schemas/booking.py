from datetime import datetime
from pydantic import BaseModel, ConfigDict


class BookingCreate(BaseModel):
    user_id: int | None = None
    service_id: str
    service_name: str
    pandit_id: str | None = None
    pandit_name: str | None = None
    scheduled_at: datetime
    address_line: str
    landmark: str | None = None
    city: str
    state: str | None = None
    pincode: str
    contact_name: str
    contact_phone: str
    notes: str | None = None
    total_inr: int


class BookingOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: str
    user_id: int | None = None
    service_id: str
    service_name: str
    pandit_id: str | None = None
    pandit_name: str | None = None
    scheduled_at: datetime
    address_line: str
    landmark: str | None = None
    city: str
    state: str | None = None
    pincode: str
    contact_name: str
    contact_phone: str
    notes: str | None = None
    total_inr: int
    status: str
    created_at: datetime


class BookingStatusUpdate(BaseModel):
    status: str
