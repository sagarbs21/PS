# PoojaSeva Backend

FastAPI + PostgreSQL backend with OTP via Twilio.

## Setup
1) Copy `.env.example` to `.env` and set Twilio creds.
2) Start services:

```bash
docker compose up --build
```

## Migrations
```bash
docker compose exec api alembic revision --autogenerate -m "init"
docker compose exec api alembic upgrade head
```

## Seed catalog
```bash
docker compose exec api python -m app.seed.seed_from_json
```

## Endpoints
- `GET /health`
- `POST /auth/otp/request`
- `POST /auth/otp/verify`
- `GET /auth/me`
- `POST /auth/logout`
- `GET /catalog/categories`
- `GET /catalog/services`
- `GET /catalog/pandits`
- `POST /bookings/`
- `GET /bookings/`
- `GET /bookings/{booking_id}`
- `PATCH /bookings/{booking_id}/status`
- `POST /payments/`
- `POST /payments/{payment_id}/confirm`
- `POST /reviews/`
- `GET /reviews/`

## Auth
Use `Authorization: Bearer <token>` for authenticated endpoints.
`POST /auth/logout` revokes the active token.

## Roles
Set `ADMIN_PHONES` (comma-separated) in `.env` to grant admin role on OTP verify.

## RBAC
- Only admins can confirm payments.
- Guests cannot submit reviews.
