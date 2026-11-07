# DrivePulse – Vehicle Maintenance & Fuel Tracker

## Overview

**DrivePulse** is a modern Android vehicle management application developed as part of a college Portfolio of Evidence (PoE). It helps vehicle owners manage vehicle specifications, track fuel consumption, record maintenance activities, monitor vehicle health scores, and review total running expenses.

The application is developed using **Kotlin** and **Jetpack Compose (Material 3)**, with a **Spring Boot 3.x REST API (Java 21)** and **MySQL** database. **Firebase Cloud Firestore** and **Supabase Storage** are incorporated into the project's cloud infrastructure.

---

## Key Application Features

### 1. Dashboard and Vehicle Health Score
- Displays active vehicle summary, driver greeting, and statistics.
- Calculates an algorithm-driven vehicle health score from **0–100** based on maintenance history, mileage, and service alerts.
- Categories: **Excellent (85–100)**, **Good (70–84)**, **Fair (50–69)**, **Poor / Attention Required (0–49)**.
- Provides practical, actionable maintenance recommendations.

### 2. Vehicle Management
- Supports multi-vehicle management with default primary vehicle selection.
- Full integration with the Spring Boot REST API and Firebase Cloud Firestore NoSQL synchronization (`vehicles` collection).

### 3. Fuel Consumption Tracking
- Records fuel fill-ups (odometer, litres, cost/litre).
- Calculates average fuel economy ($\text{L/100km}$ or $\text{mpg}$) with zero-distance protection.
- Tracks fuel expenses across vehicles.

### 4. Maintenance and Service Records
- Records vehicle maintenance activities, service costs, and odometer readings.
- Tracks upcoming service due targets and service history logs.

### 5. Cloud File Storage (Supabase Storage)
- Integrated Supabase Storage with private `vehicle-documents` storage bucket.
- Enforces a maximum file upload size of $10\text{ MB}$.
- Supports JPEG, PNG, and PDF document formats.
- Generates unique UUID file names to prevent file overwrites.

### 6. Firebase Cloud Firestore
- Configured Cloud Firestore in the Johannesburg region (`af-south-1`).
- Operates on the `"vehicles"` collection.
- Reads and writes vehicle document records (`vehicle_001` mapped to model).

### 7. Settings and Preferences
- Configurable App Theme (**System Default**, **Light**, **Dark**).
- Currency Selector (**ZAR R**, **USD $**, **EUR €**, **GBP £**).
- Measurement Unit Selectors (**Kilometres / Miles**, **Litres / Gallons**).
- Service Reminder Notification preferences and Account Logout.

### 8. Multi-Language Support (English & Tshivenda)
- **English** (Default)
- **Tshivenda** (South African Language)
- Dynamic language switcher in Settings using Android string resources.

### 9. Local Persistence and Security
- Local disk persistence using `SharedPreferences` + `Gson` and `Room`.
- BCrypt password hashing in the Spring Boot backend and SHA-256 password hashing in Android.
- Reads secret credentials from environment variables (`SUPABASE_URL`, `SUPABASE_SERVICE_KEY`).

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Kotlin | Android application development |
| Jetpack Compose | Android user interface |
| Room Database | Local data persistence |
| Retrofit 2 | REST API communication |
| Spring Boot 3.x | Backend REST API development |
| Java 21 | Backend programming language |
| MySQL | Relational database persistence |
| Firebase Firestore | Cloud NoSQL document database |
| Supabase Storage | Cloud blob document storage |
| GitHub | Source code management |
| GitHub Actions | Automated CI/CD build and testing |

---

## System Architecture

```text
DrivePulse Android Application (Kotlin + Jetpack Compose)
    │
    ├── Presentation Layer: Jetpack Compose (MVVM Architecture)
    ├── Local Storage: Room Database + SharedPreferences (Gson)
    ├── Network Layer: Retrofit 2 + Gson Converter
    └── Cloud Layer: Firebase Cloud Firestore + Firebase Auth
            │
            ▼
Spring Boot REST API (Java 21)
    │
    ├── Controllers: REST Endpoints
    ├── Services: Business Logic & Supabase Integration
    ├── Repositories: Spring Data JPA
    │
    ├── Database: MySQL Database (`drivepulse_db`)
    └── Blob Storage: Supabase Storage API (`vehicle-documents`)
```

---

## Testing and Quality Assurance

Testing activities include Android unit tests, Spring Boot backend unit tests, and REST API testing via Postman.

### Android Application Unit Tests (`:app:testDebugUnitTest`)
- **Total Tests:** **38 Passed / 0 Failed**
- **Test Classes:** `AuthValidationTest`, `FuelCalculatorTest`, `HealthScoreCalculatorTest`, `MaintenanceValidatorTest`, `ReportCalculatorTest`, `SettingsPreferencesTest`, `VehicleValidatorTest`, `NetworkResultTest`.

### Spring Boot Backend Unit Tests (`drivepulse-api`)
- **Total Tests:** **10 Passed / 0 Failed**
- **Test Classes:** `DrivepulseApiApplicationTests`, `SupabaseStorageServiceTest`, `VehicleHealthServiceTest`.

---

## Running the Project

### 1. Spring Boot Backend

Navigate to the backend project directory:

```bash
cd drivepulse-api
```

Configure required environment variables:

```powershell
$env:SUPABASE_URL="https://YOUR_SUPABASE_URL.supabase.co"
$env:SUPABASE_SERVICE_KEY="YOUR_SUPABASE_SERVICE_ROLE_KEY"
```

Start the Spring Boot application using the Maven wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

### 2. Android Application

Open the project in Android Studio, sync Gradle files, and deploy to an Android device or emulator running API 26+.

To build the debug APK from terminal:

```powershell
.\gradlew assembleDebug
```

To run unit tests:

```powershell
.\gradlew testDebugUnitTest
```

---

## GitHub and Version Control

- **Repository:** https://github.com/mutshidzi-nelufule/DrivePulse
- **Development Branch:** `part3-development`
- **Release Tag:** `final-PoE`

---

## Licence

Developed for college Portfolio of Evidence (PoE) assessment.
