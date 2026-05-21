from sqlalchemy import Column, DateTime, Integer, String, func

from app.models.base import Base


class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    phone = Column(String(20), unique=True, index=True, nullable=False)
    role = Column(String(20), default="user", nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
