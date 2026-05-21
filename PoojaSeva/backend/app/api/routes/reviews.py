from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.api.deps import get_current_user, get_db
from app.models.review import Review
from app.models.user import User
from app.schemas.review import ReviewCreate, ReviewOut

router = APIRouter()


@router.post("/", response_model=ReviewOut)
def create_review(
    payload: ReviewCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
) -> ReviewOut:
    if current_user.role == "guest":
        raise HTTPException(status_code=403, detail="Forbidden")
    review = Review(
        service_id=payload.service_id,
        user_id=current_user.id,
        rating=payload.rating,
        comment=payload.comment,
    )
    db.add(review)
    db.commit()
    db.refresh(review)
    return review


@router.get("/", response_model=list[ReviewOut])
def list_reviews(db: Session = Depends(get_db)) -> list[ReviewOut]:
    return db.query(Review).order_by(Review.created_at.desc()).all()
