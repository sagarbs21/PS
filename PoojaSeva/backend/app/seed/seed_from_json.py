import json
from pathlib import Path

from sqlalchemy.orm import Session

from app.core.database import SessionLocal
from app.models.catalog import Category, Pandit, Service


def _candidate_paths() -> list[Path]:
    here = Path(__file__).resolve()
    return [
        here.parent / "services.json",  # bundled copy (works inside the Docker image)
        here.parents[3] / "app" / "src" / "main" / "assets" / "services.json",  # local repo layout
    ]


def load_seed() -> dict:
    for seed_path in _candidate_paths():
        if seed_path.exists():
            with seed_path.open("r", encoding="utf-8") as f:
                return json.load(f)
    searched = ", ".join(str(p) for p in _candidate_paths())
    raise FileNotFoundError(f"services.json seed file not found. Searched: {searched}")


def seed(db: Session) -> None:
    data = load_seed()
    for cat in data.get("categories", []):
        db.merge(Category(
            id=cat["id"],
            name=cat["name"],
            tagline=cat["tagline"],
            icon_key=cat["iconKey"],
        ))
    for svc in data.get("services", []):
        db.merge(Service(
            id=svc["id"],
            category_id=svc["categoryId"],
            name=svc["name"],
            short_description=svc["shortDescription"],
            description=svc["description"],
            vidhi=svc["vidhi"],
            samagri=svc["samagri"],
            duration_minutes=svc["durationMinutes"],
            suggested_time=svc["suggestedTime"],
            price_inr=svc["priceInr"],
            is_featured=svc.get("isFeatured", False),
            rating=svc.get("rating", 4.7),
            reviews_count=svc.get("reviewsCount", 0),
        ))
    for pandit in data.get("pandits", []):
        db.merge(Pandit(
            id=pandit["id"],
            name=pandit["name"],
            experience_years=pandit["experienceYears"],
            languages=pandit["languages"],
            specializations=pandit["specializations"],
            rating=pandit["rating"],
            reviews_count=pandit["reviewsCount"],
            price_multiplier=pandit.get("priceMultiplier", 1.0),
        ))
    db.commit()


if __name__ == "__main__":
    session = SessionLocal()
    try:
        seed(session)
    finally:
        session.close()
