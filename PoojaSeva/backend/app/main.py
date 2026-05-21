from fastapi import FastAPI

from app.api.routes import auth, bookings, catalog, health, payments, reviews

app = FastAPI(title="PoojaSeva API", version="0.1.0")

app.include_router(health.router)
app.include_router(auth.router, prefix="/auth", tags=["auth"])
app.include_router(catalog.router, prefix="/catalog", tags=["catalog"])
app.include_router(bookings.router, prefix="/bookings", tags=["bookings"])
app.include_router(payments.router, prefix="/payments", tags=["payments"])
app.include_router(reviews.router, prefix="/reviews", tags=["reviews"])
