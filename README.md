# DishDiscover – Recipe Explorer

A simple Android recipe app built with Kotlin and Jetpack Compose. The app allows users to browse, search, and view recipe details using the [DummyJSON API](https://dummyjson.com/recipes).

## Requirements

- Android Studio
- JDK 17
- Android SDK 36
- Android device or emulator (API 24 or above)
- Internet connection

## Setup and Running

1. Clone the repository:

   ```bash
   git clone https://github.com/naufalsrn/star-recipe-explorer.git
   ```

2. Open the project in Android Studio.

3. Wait for Gradle Sync to complete.

4. Connect an Android device or start an emulator.

5. Click **Run** in Android Studio.

To build the APK using the terminal:

```bash
# Windows
.\gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

The APK will be generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Assumptions

- Recipe data is retrieved from the DummyJSON API.
- An internet connection is required to load recipes and images.
- The difficulty filter applies to the recipes currently loaded.
- Search results are retrieved from the API.
- Recipe data and ingredient checklist selections are stored temporarily in memory.
- No user login or account registration is required.

## Features
- Recipe list with image and title
- Recipe details screen
- Search (API `q` parameter)
- Difficulty filter (client-side)
- Pull-to-refresh
- Pagination with infinite scroll (10 per page)
- Error handling with retry