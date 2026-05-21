from sqlalchemy import Column, DateTime, Integer, String, func

from app.models.base import Base


class Booking(Base):
    __tablename__ = "bookings"

    id = Column(String(64), primary_key=True)
    user_id = Column(Integer, nullable=True, index=True)
    service_id = Column(String(50), nullable=False, index=True)
    service_name = Column(String(200), nullable=False)
    pandit_id = Column(String(50), nullable=True, index=True)
    pandit_name = Column(String(200), nullable=True)
    scheduled_at = Column(DateTime(timezone=True), nullable=False)
    address_line = Column(String(300), nullable=False)
    landmark = Column(String(300), nullable=True)
    city = Column(String(100), nullable=False)
    state = Column(String(100), nullable=True)
    pincode = Column(String(10), nullable=False)
    contact_name = Column(String(100), nullable=False)
    contact_phone = Column(String(20), nullable=False)
    notes = Column(String(1000), nullable=True)
    total_inr = Column(Integer, nullable=False)
    status = Column(String(30), default="Pending", nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
