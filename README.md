# DrivePulse — Vehicle Maintenance & Fuel Tracking Android App

## Overview
**DrivePulse** is a college Portfolio of Evidence (PoE) Android application developed using **Kotlin** and **Jetpack Compose**. It helps vehicle owners manage vehicle specifications, track fuel fill-ups, calculate fuel consumption efficiency (`L/100km`), log maintenance services, monitor upcoming service alerts, and view total running cost reports.

---

## Features
- **User Authentication:** User registration, secure login/logout, session persistence (`SharedPreferences`), BCrypt password hashing, and input validation.
- **Dashboard:** Welcome summary, Vehicle Health Score (0–100 indicator with service alerts), quick stats (Odometer, Avg L/100km, Total Costs), and quick action dialogs (`Log Fuel`, `Log Maintenance`).
- **Vehicle Management:** Add, edit, delete, view vehicle details, and select primary default vehicle with input validation.
- **Fuel Tracking & Consumption Efficiency:** Fuel fill-up log history, automatic total cost calculation, and `L/100km` efficiency calculations with division-by-zero safeguards.
- **Maintenance & Service Tracking:** Category selection (Oil change, Tyre service, Brake service, Battery, Air filter, General service, Repairs), service cost tracking, and upcoming service odometer reminders.
- **Financial Reports & Analytics:** Total vehicle running expenses, Cost per Kilometre (`R/km`), and visual proportion bar for Fuel vs Maintenance expenditure.
- **Settings & Regional Preferences:** Theme modes (Light / Dark / System), Currency options (ZAR R, USD $, EUR €, GBP £), Measurement units (KM vs Miles, Litres vs Gallons), Notification toggle, and South African language preference (English & Tshivenda).
- **REST API & Room Database Preparation:** Retrofit 2 networking client, Spring Boot-aligned DTOs, configurable `BASE_URL`, and Room local database entities and DAOs.

---

## Technologies & Architecture
- **Language:** Kotlin 2.2.10
- **UI Toolkit:** Jetpack Compose (Material 3)
- **Architecture:** Clean Architecture MVVM (Model-View-ViewModel)
- **Navigation:** Navigation Compose
- **Networking:** Retrofit 2, OkHttp 4, Gson
- **Local Persistence:** Room Database, SharedPreferences
- **Testing:** JUnit 4 unit tests (38 tests passed)
- **CI/CD:** GitHub Actions workflow

---

## Unit Testing
DrivePulse includes 38 comprehensive unit tests covering:
- Authentication input validation (Name, Email format, Password strength)
- BCrypt password hashing determinism & security
- Vehicle input validation (Make, Model, Year range, Odometer, VIN)
- Fuel calculations & edge cases (Total cost, `L/100km` efficiency, zero-distance handling, negative odometer handling)
- Maintenance validation (Cost, Odometer, Next service odometer)
- Vehicle Health Score algorithm (Overdue deductions, upcoming alerts, score clamping)
- Financial report calculations (Cost per km, expense proportions)
- Settings preferences and currency symbol formatting
- Network result state wrappers

To run unit tests locally:
```bash
./gradlew testDebugUnitTest
```

---

## GitHub Actions CI Workflow
Automated CI build and unit test execution is configured in `.github/workflows/build.yml`. On every `push` and `pull_request`, GitHub Actions executes:
1. JDK 17 Environment Setup
2. `./gradlew assembleDebug`
3. `./gradlew testDebugUnitTest`

---

## Installation & How to Run
1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/DrivePulse.git
   ```
2. Open the project in **Android Studio (Ladybug / Jellyfish or newer)**.
3. Allow Gradle to sync dependencies.
4. Select an Android Virtual Device (Emulator API 24+) or physical device.
5. Click **Run** (Shift + F10) or build via terminal:
   ```bash
   ./gradlew assembleDebug
   ```

---

## Project Structure
```
com.example.drivepulse/
├── MainActivity.kt
├── DrivePulseApp.kt
├── data/
│   ├── local/ (SessionManager, SettingsManager, Room DB/Entities/DAOs)
│   ├── model/ (User, Vehicle, FuelRecord, MaintenanceRecord, Reminder)
│   ├── remote/ (Api, ApiClient, DTOs)
│   └── repository/ (Auth, Vehicle, Fuel, Maintenance, Settings)
├── ui/
│   ├── components/ (Reusable Jetpack Compose UI)
│   ├── navigation/ (DrivePulseNavigation, Screen)
│   ├── screens/ (Auth, Dashboard, Vehicles, Fuel, Maintenance, Reports, Settings)
│   ├── theme/ (Color, Theme, Type)
│   └── viewmodel/ (Auth, Vehicle, Fuel, Maintenance, Dashboard, Reports, Settings)
└── util/ (FuelCalculator, HealthScoreCalculator, ValidationUtils, ReportCalculator, NetworkResult)
```

## YouTube Link
https://youtu.be/4vP9X-BOSuY?si=_kbk0TVnqE47f1Cg 

---

## License
Created for academic portfolio and assessment purposes.
