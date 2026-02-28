# 🎵 Paatuku Paatu

A beautiful, feature-rich music player for Android built with modern Android development practices.

---

## Screenshots

| Home Screen | Now Playing | Favorites |
|:-----------:|:-----------:|:---------:|
| ![Home](screenshots/home.png) | ![Now Playing](screenshots/nowplaying.png) | ![Favorites](screenshots/favorites.png) |

| Search | Empty State | About |
|:------:|:-----------:|:-----:|
| ![Search](screenshots/search.png) | ![Empty](screenshots/empty_state.png) | ![About](screenshots/about.png) |

| Notification | Mini Player |
|:------------:|:-----------:|
| ![Notification](screenshots/notification.png) | ![Mini Player](screenshots/mini_player.png) |

> **Note:** Add screenshots to a `screenshots/` folder in the project root.

---

## Features

- **Music Playback** — Play, pause, skip next/previous with full playback controls
- **Seek Bar** — Drag to seek through any track with real-time position display
- **Media Notification** — Control playback from the notification shade (play/pause, next, previous)
- **Mini Player** — Persistent mini player bar with progress indicator across all screens
- **Favorites** — Mark songs as favorites and access them from a dedicated screen
- **Search** — Search songs by title, artist, or album name
- **Song Metadata** — Display title, artist, album, album art, duration, format, and file size
- **Album Art** — Loaded via Coil with graceful fallback icons
- **ML Recommendations** — Local recommendation engine suggests songs based on listening history, play counts, completion rates, and favorites
- **Ad Cards** — Optional ad card display during playback, toggled via `local.properties`
- **File Association** — Open audio files directly from the file manager
- **Multi-Format Support** — MP3, M4A, WAV, FLAC, OGG, AAC, OPUS, WMA, AMR
- **Empty State Handling** — Clear messaging when no music files are found on the device

---

## Architecture

The project follows **Multi-Module Clean Architecture** with **MVVM** pattern. All modules are flat at the root level for simplicity.

```
Paatuku-Paatu/
├── app/            → Application entry point, navigation, DI wiring, theme
├── mediaQuery/     → MediaStore queries, Audio data model
├── database/       → Room database, entities, DAOs, repositories
├── player/         → Music playback service, foreground notification, player controller
├── home/           → Home screen, song list, search, ML recommendation engine
├── nowplaying/     → Full-screen player, controls, seek bar, ad card
├── favorites/      → Favorites list screen
├── about/          → App info and tech stack display
```

### Module Dependency Graph

```
app
 ├── mediaQuery
 ├── database ── mediaQuery
 ├── player ──── mediaQuery
 ├── home ────── mediaQuery, database, player
 ├── nowplaying ─ mediaQuery, database, player
 ├── favorites ── mediaQuery, database, player
 └── about (standalone)
```

---

## Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Kotlin 1.9.0 |
| **UI** | Jetpack Compose + Material 3 |
| **DI** | Koin 3.4.3 |
| **Image Loading** | Coil 2.4.0 |
| **Database** | Room 2.6.1 |
| **Navigation** | Jetpack Navigation Compose 2.6.0 |
| **Async** | Kotlin Coroutines + Flow |
| **Media** | AndroidX Media 1.6.0 + MediaPlayer |
| **Permissions** | Accompanist Permissions 0.31.1 |
| **Code Style** | Spotless |
| **Annotation Processing** | KSP 1.9.0-1.0.13 |
| **Build System** | Gradle 8.7 + AGP 8.5.2 |

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or higher
- Android SDK 34
- A physical device or emulator running Android 10+ (API 29+)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/Paatuku-Paatu.git
   cd Paatuku-Paatu
   ```

2. **Configure ads (optional)**

   Add the following to `local.properties`:
   ```properties
   ADS_ENABLED=true
   ```
   Set to `false` or omit to disable ad cards.

3. **Build and run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or open the project in Android Studio and run the `app` configuration.

---

## Permissions

| Permission | Purpose |
|------------|---------|
| `READ_MEDIA_AUDIO` | Access audio files on Android 13+ |
| `READ_EXTERNAL_STORAGE` | Access audio files on older Android versions |
| `FOREGROUND_SERVICE` | Run music playback as a foreground service |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Media-type foreground service on Android 14+ |
| `POST_NOTIFICATIONS` | Display playback notification |

---

## ML Recommendation Engine

The app includes a fully local recommendation engine that scores songs based on:

- **Play count** — Songs played more frequently score higher
- **Completion rate** — Songs listened to completion (>80%) are weighted more
- **Favorites** — Favorited songs get a significant score boost
- **Artist affinity** — Songs from frequently played artists score higher
- **Skip penalty** — Frequently skipped songs are ranked lower
- **Novelty bonus** — Unplayed songs get a small boost to encourage discovery
- **Cold start** — When no listening history exists, random songs are suggested

All recommendation data is stored locally using Room — no data leaves the device.

---

## Project Configuration

| Property | Value |
|----------|-------|
| Application ID | `com.workspace.paatukupaatu` |
| Min SDK | 29 (Android 10) |
| Target SDK | 34 (Android 14) |
| Compile SDK | 34 |
| Kotlin | 1.9.0 |
| Compose Compiler | 1.5.1 |
| Compose BOM | 2023.03.00 |

---

## License

This project is licensed under the terms included in the [LICENSE](LICENSE) file.
