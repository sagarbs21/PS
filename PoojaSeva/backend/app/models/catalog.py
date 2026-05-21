from sqlalchemy import Boolean, Column, Float, Integer, String
from sqlalchemy.dialects.postgresql import JSONB

from app.models.base import Base


class Category(Base):
    __tablename__ = "categories"

    id = Column(String(50), primary_key=True)
    name = Column(String(200), nullable=False)
    tagline = Column(String(300), nullable=False)
    icon_key = Column(String(100), nullable=False)


class Service(Base):
    __tablename__ = "services"

    id = Column(String(50), primary_key=True)
    category_id = Column(String(50), nullable=False, index=True)
    name = Column(String(200), nullable=False)
    short_description = Column(String(300), nullable=False)
    description = Column(String(2000), nullable=False)
    vidhi = Column(JSONB, nullable=False, default=list)
    samagri = Column(JSONB, nullable=False, default=list)
    duration_minutes = Column(Integer, nullable=False)
    suggested_time = Column(String(100), nullable=False)
    price_inr = Column(Integer, nullable=False)
    is_featured = Column(Boolean, default=False, nullable=False)
    rating = Column(Float, default=4.7, nullable=False)
    reviews_count = Column(Integer, default=0, nullable=False)


class Pandit(Base):
    __tablename__ = "pandits"

    id = Column(String(50), primary_key=True)
    name = Column(String(200), nullable=False)
    experience_years = Column(Integer, nullable=False)
    languages = Column(JSONB, nullable=False, default=list)
    specializations = Column(JSONB, nullable=False, default=list)
    rating = Column(Float, default=4.8, nullable=False)
    reviews_count = Column(Integer, default=0, nullable=False)
    price_multiplier = Column(Float, default=1.0, nullable=False)
