from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.api.deps import get_db
from app.models.catalog import Category, Pandit, Service
from app.schemas.catalog import CategoryOut, PanditOut, ServiceOut

router = APIRouter()


@router.get("/categories", response_model=list[CategoryOut])
def list_categories(db: Session = Depends(get_db)) -> list[CategoryOut]:
    return db.query(Category).all()


@router.get("/services", response_model=list[ServiceOut])
def list_services(db: Session = Depends(get_db)) -> list[ServiceOut]:
    return db.query(Service).all()


@router.get("/pandits", response_model=list[PanditOut])
def list_pandits(db: Session = Depends(get_db)) -> list[PanditOut]:
    return db.query(Pandit).all()
