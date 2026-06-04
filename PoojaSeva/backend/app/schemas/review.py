from datetime import datetime
from pydantic import BaseModel, ConfigDict


class ReviewCreate(BaseModel):
    service_id: str
    user_id: int | None = None
    rating: float
    comment: str | None = None


class ReviewOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    service_id: str
    user_id: int | None = None
    rating: float
    comment: str | None = None
    created_at: datetime
