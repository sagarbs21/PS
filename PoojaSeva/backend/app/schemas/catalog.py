from pydantic import BaseModel


class CategoryOut(BaseModel):
    id: str
    name: str
    tagline: str
    icon_key: str


class ServiceOut(BaseModel):
    id: str
    category_id: str
    name: str
    short_description: str
    description: str
    vidhi: list[str]
    samagri: list[str]
    duration_minutes: int
    suggested_time: str
    price_inr: int
    is_featured: bool
    rating: float
    reviews_count: int


class PanditOut(BaseModel):
    id: str
    name: str
    experience_years: int
    languages: list[str]
    specializations: list[str]
    rating: float
    reviews_count: int
    price_multiplier: float
