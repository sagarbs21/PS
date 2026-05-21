from sqlalchemy import Column, DateTime, Float, Integer, String, func

from app.models.base import Base


class Review(Base):
    __tablename__ = "reviews"

    id = Column(Integer, primary_key=True, index=True)
    service_id = Column(String(50), nullable=False, index=True)
    user_id = Column(Integer, nullable=True, index=True)
    rating = Column(Float, nullable=False)
    comment = Column(String(1000), nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
