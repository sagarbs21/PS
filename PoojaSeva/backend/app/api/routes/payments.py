import uuid
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.api.deps import get_current_user, get_db
from app.models.payment import Payment
from app.models.user import User
from app.schemas.payment import PaymentCreate, PaymentOut

router = APIRouter()


@router.post("/", response_model=PaymentOut)
def create_payment(
    payload: PaymentCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
) -> PaymentOut:
    payment = Payment(
        id=f"PAY_{uuid.uuid4().hex[:10]}",
        booking_id=payload.booking_id,
        amount_inr=payload.amount_inr,
        status="Created",
        provider=payload.method,
    )
    db.add(payment)
    db.commit()
    db.refresh(payment)
    return payment


@router.post("/{payment_id}/confirm", response_model=PaymentOut)
def confirm_payment(
    payment_id: str,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
) -> PaymentOut:
    if current_user.role != "admin":
        raise HTTPException(status_code=403, detail="Forbidden")
    payment = db.query(Payment).filter(Payment.id == payment_id).first()
    if payment is None:
        raise HTTPException(status_code=404, detail="Payment not found")
    payment.status = "Confirmed"
    payment.txn_id = f"TXN_{uuid.uuid4().hex[:10].upper()}"
    db.commit()
    db.refresh(payment)
    return payment
