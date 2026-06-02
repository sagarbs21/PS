import logging
import time
from contextlib import asynccontextmanager

from fastapi import FastAPI
from sqlalchemy.exc import OperationalError

from app import models  # noqa: F401  (registers all tables on Base.metadata)
from app.api.routes import auth, bookings, catalog, health, payments, reviews
from app.core.database import SessionLocal, engine
from app.models.base import Base
from app.models.catalog import Category

logger = logging.getLogger(__name__)


def init_db(retries: int = 12, delay: float = 2.0) -> None:
    """Create tables if they don't exist, waiting for the DB to accept connections."""
    last_err: Exception | None = None
    for attempt in range(1, retries + 1):
        try:
            Base.metadata.create_all(bind=engine)
            return
        except OperationalError as exc:
            last_err = exc
            logger.warning("Database not ready (attempt %s/%s), retrying...", attempt, retries)
            time.sleep(delay)
    if last_err is not None:
        raise last_err


def seed_if_empty() -> None:
    """Populate the catalog on first boot so a fresh DB is usable immediately."""
    from app.seed.seed_from_json import seed

    db = SessionLocal()
    try:
        if db.query(Category).count() == 0:
            seed(db)
            logger.info("Catalog seeded on startup.")
    except Exception:
        logger.exception("Catalog auto-seed skipped (continuing).")
    finally:
        db.close()


@asynccontextmanager
async def lifespan(app: FastAPI):
    init_db()
    seed_if_empty()
    yield


app = FastAPI(title="PoojaSeva API", version="0.1.0", lifespan=lifespan)

app.include_router(health.router)
app.include_router(auth.router, prefix="/auth", tags=["auth"])
app.include_router(catalog.router, prefix="/catalog", tags=["catalog"])
app.include_router(bookings.router, prefix="/bookings", tags=["bookings"])
app.include_router(payments.router, prefix="/payments", tags=["payments"])
app.include_router(reviews.router, prefix="/reviews", tags=["reviews"])
