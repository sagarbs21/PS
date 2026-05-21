from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    env: str = "local"
    database_url: str

    jwt_secret_key: str
    jwt_algorithm: str = "HS256"
    jwt_access_token_expires_minutes: int = 60

    twilio_account_sid: str
    twilio_auth_token: str
    twilio_from_number: str
    otp_expires_minutes: int = 5

    otp_mode: str = "twilio"  # twilio | email | stub
    smtp_host: str = ""
    smtp_port: int = 587
    smtp_user: str = ""
    smtp_password: str = ""
    smtp_from: str = ""

    admin_phones: str = ""

    class Config:
        env_file = ".env"
        case_sensitive = False

    def admin_phone_set(self) -> set[str]:
        return {p.strip() for p in self.admin_phones.split(",") if p.strip()}


settings = Settings()
