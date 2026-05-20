# PoojaSeva — Android App

Modern Kotlin + Jetpack Compose app that lets devotees discover, book, and pay for traditional pooja services with verified pandits.

## Highlights
- **Stack**: Kotlin 2.0, Jetpack Compose (Material 3), Hilt DI, Navigation-Compose, Kotlinx Serialization, Coroutines/Flow, Coil.
- **Architecture**: Clean Architecture (`data` / `domain` / `ui`) + MVVM with `StateFlow`-driven UI.
- **Theme**: "Modern Minimal Devotional" — soft saffron (`#F2994A`), deep indigo (`#1E1B4B`), ivory (`#FFF8EC`), gold (`#C9A227`); rounded shapes, mandala / diya / Om motifs as vector drawables.
- **Data**: 100% offline MVP — services, categories, and pandits seeded from `app/src/main/assets/services.json`. Repository interfaces are designed so a real REST/Firebase backend can be plugged in by adding new implementations and rebinding in `di/RepositoryModule.kt`.
- **Auth**: Phone-OTP UI flow + "Continue as Guest". Backed by `StubAuthRepository` (use OTP `123456`).
- **Payment**: `PaymentGateway` interface with a `FakePaymentGateway` simulator. Swap in Razorpay/UPI by implementing the interface.
- **Languages**: English strings complete; Hindi, Kannada, Tamil, Telugu skeleton string files included.

## Project layout
```
app/src/main/
  assets/services.json            # seed catalog
  java/com/poojaseva/
    PoojaSevaApp.kt               # @HiltAndroidApp
    MainActivity.kt
    data/                         # repos + seed loader
    domain/                       # models + repository interfaces
    di/                           # Hilt modules
    navigation/                   # routes + NavGraph
    ui/
      theme/                      # Color, Type, Shape, Theme
      components/                 # PoojaCard, CategoryChip, MandalaDivider, etc.
      screens/
        splash/ onboarding/ auth/
        home/ list/ detail/
        pandit/ booking/ payment/ confirmation/
        orders/ profile/
  res/
    values/strings.xml            # en
    values-hi|kn|ta|te/strings.xml
    drawable/ic_diya.xml ic_mandala.xml ic_om.xml
```

## Service catalog (seeded)
6 categories — Griha Pravesh, Vivaha Sanskar, Satyanarayan, Navagraha / Graha Shanti, Festival Poojas, Antim Sanskar — and 12 starter services with vidhi steps, samagri checklists, durations, suggested timings, and prices. 5 sample pandits.

## Build & run
1. Open the `PoojaSeva/` folder in **Android Studio Hedgehog (or newer)** with **AGP 8.5.x** and **JDK 17**.
2. Let Gradle sync (it will download Compose BOM, Hilt, Room, etc.).
3. Generate the Gradle wrapper if missing: `gradle wrapper --gradle-version 8.7` (or use Studio's "File → Sync Project with Gradle Files").
4. Run the `app` configuration on an emulator (Pixel 6 / API 34 recommended).

> **Note**: The Gradle wrapper JAR is intentionally not committed. Run `gradle wrapper` once locally — Android Studio does this automatically on first sync.

## Test credentials
- **Phone**: any 10-digit number
- **OTP**: `123456`

## Plugging in a real backend
1. Implement `domain/repository/*` interfaces in `data/remote/` (Retrofit / Ktor / Firebase, your choice).
2. Update `di/RepositoryModule.kt` `@Binds` to point to the new implementations.
3. Add `INTERNET` permission is already declared.

## Plugging in a real payment gateway
Replace `FakePaymentGateway` with e.g. a `RazorpayGateway` implementing `PaymentGateway`, then rebind in `RepositoryModule`. Keep API keys in `local.properties` — never commit them.

## Roadmap
- Persist bookings in Room (DB skeleton already wired via dependencies).
- Real OTP via Firebase Auth or MSG91.
- Push notifications for booking status (FCM).
- Pandit-side companion app.
- Tablet / large-screen layouts.
