# DrivePulse — Final PoE Release Notes

## Version 1.0.0 (Final PoE Release)

### 🚀 What's New & Major Enhancements

1. **Supabase Blob Storage Integration:**
   - Integrated server-side file upload, download, and deletion via Spring Boot backend (`drivepulse-api`).
   - Connects securely to the `vehicle-documents` bucket in Supabase.
   - Enforces $10\text{ MB}$ max file size and allows JPEG, PNG, and PDF document formats.
   - Credentials secured via `SUPABASE_URL` and `SUPABASE_SERVICE_KEY` environment variables.

2. **Firebase Cloud Firestore NoSQL Database:**
   - Synchronizes vehicle records to Cloud Firestore (`vehicles` collection) in the Johannesburg region.
   - Documents such as `vehicle_001` (`Toyota Corolla 2020`) automatically map to the app domain model.

3. **Multi-Language Support (English & Tshivenda):**
   - Added full string resources for **English** (`values/strings.xml`) and **Tshivenda** (`values-ve/strings.xml`).
   - Dynamic locale switcher integrated into Settings.

4. **Refined Vehicle Health Score Feature (0–100):**
   - Score calculated using mileage, overdue service penalties (-20 pts), upcoming service alerts (-10 pts), and missing history logs (-15 pts).
   - Displayed with status badges (**Excellent**, **Good**, **Fair**, **Poor**) and actionable recommendations.
   - Logic synchronized across Android (`HealthScoreCalculator.kt`) and Spring Boot (`VehicleHealthService.java`).

5. **Top Bar Settings Navigation & Preferences:**
   - Added a Top App Bar **Settings Button** on main screens.
   - Customize App Theme (System/Light/Dark), Currency (ZAR/USD/EUR/GBP), Distance Units (km/mi), Fuel Units (L/gal), and Primary Vehicle.

6. **Offline-First Storage & Network Resilience:**
   - Local disk fallback using `SharedPreferences` + `Gson` and `Room`.
   - App works seamlessly offline or when the backend server is unreachable.

---

### 🧪 Verification Summary
- **Android Unit Tests:** 38 / 38 Passed
- **Backend Unit Tests:** 10 / 10 Passed
- **Build Status:** Success (`assembleDebug` & `mvnw test`)
