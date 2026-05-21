import uuid
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.api.deps import get_current_user, get_db
from app.models.booking import Booking
from app.models.user import User
from app.schemas.booking import BookingCreate, BookingOut, BookingStatusUpdate

router = APIRouter()


@router.post("/", response_model=BookingOut)
def create_booking(
    payload: BookingCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
) -> BookingOut:
    booking = Booking(
        id=f"BKG_{uuid.uuid4().hex[:10]}",
        user_id=current_user.id,
        service_id=payload.service_id,
        service_name=payload.service_name,
        pandit_id=payload.pandit_id,
        pandit_name=payload.pandit_name,
        scheduled_at=payload.scheduled_at,
        address_line=payload.address_line,
        landmark=payload.landmark,
        city=payload.city,
        state=payload.state,
        pincode=payload.pincode,
        contact_name=payload.contact_name,
        contact_phone=payload.contact_phone,
        notes=payload.notes,
        total_inr=payload.total_inr,
        status="Pending",
    )
    db.add(booking)
    db.commit()
    db.refresh(booking)
    return booking


@router.get("/", response_model=list[BookingOut])
def list_bookings(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
) -> list[BookingOut]:
    query = db.query(Booking).order_by(Booking.created_at.desc())
    if current_user.role != "admin":
        query = query.filter(Booking.user_id == current_user.id)
    return query.all()


@router.get("/{booking_id}", response_model=BookingOut)
def get_booking(
    booking_id: str,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
) -> BookingOut:
    booking = db.query(Booking).filter(Booking.id == booking_id).first()
    if booking is None:
        raise HTTPException(status_code=404, detail="Booking not found")
    if current_user.role != "admin" and booking.user_id != current_user.id:
        raise HTTPException(status_code=403, detail="Forbidden")
    return booking


@router.patch("/{booking_id}/status", response_model=BookingOut)
def update_booking_status(
    booking_id: str,
    payload: BookingStatusUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
) -> BookingOut:
    booking = db.query(Booking).filter(Booking.id == booking_id).first()
    if booking is None:
        raise HTTPException(status_code=404, detail="Booking not found")
    if current_user.role != "admin" and booking.user_id != current_user.id:
        raise HTTPException(status_code=403, detail="Forbidden")
    booking.status = payload.status
    db.commit()
    db.refresh(booking)
    return booking
