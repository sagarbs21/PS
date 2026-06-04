from sqlalchemy import Column, DateTime, Integer, String, func

from app.models.base import Base


class Payment(Base):
    __tablename__ = "payments"

    id = Column(String(64), primary_key=True)
    booking_id = Column(String(64), nullable=False, index=True)
    amount_inr = Column(Integer, nullable=False)
    status = Column(String(30), default="Created", nullable=False)
    provider = Column(String(50), default="manual", nullable=False)
    txn_id = Column(String(100), nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
