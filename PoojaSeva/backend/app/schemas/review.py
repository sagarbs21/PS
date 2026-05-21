from datetime import datetime
from pydantic import BaseModel


class ReviewCreate(BaseModel):
    service_id: str
    user_id: int | None = None
    rating: float
    comment: str | None = None


class ReviewOut(BaseModel):
    id: int
    service_id: str
    user_id: int | None = None
    rating: float
    comment: str | None = None
    created_at: datetime
