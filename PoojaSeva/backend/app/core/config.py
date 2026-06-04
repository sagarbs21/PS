from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", case_sensitive=False, extra="ignore")

    env: str = "local"
    database_url: str

    jwt_secret_key: str
    jwt_algorithm: str = "HS256"
    jwt_access_token_expires_minutes: int = 60

    otp_expires_minutes: int = 5
    otp_max_attempts: int = 5
    otp_mode: str = "email"  # email (Resend) | stub

    # Email delivery via Resend (HTTP API; works on hosts that block SMTP).
    resend_api_key: str = ""
    email_from: str = "PoojaSeva <onboarding@resend.dev>"

    admin_phones: str = ""

    def admin_phone_set(self) -> set[str]:
        return {p.strip() for p in self.admin_phones.split(",") if p.strip()}


settings = Settings()
