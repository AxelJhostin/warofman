# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**War of Men** is an Android RPG fitness tracker that gamifies physical exercise. Users create a character whose initial stats (STR, STA, AGI, WIL, LUK) are derived from real biometric data (weight, height, age). Completing workouts ("Misiones") earns XP, levels up the character, and boosts stats. All data is stored locally.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run a single unit test class
./gradlew test --tests "com.axeljhostin.warofmen.ExampleUnitTest"

# Clean build
./gradlew clean assembleDebug
```

Use `gradlew.bat` instead of `./gradlew` on Windows CMD. The Gradle wrapper is at the project root.

## Architecture

**MVVM + Clean Architecture (simplified)**, with three layers:

### `core/`
- `navigation/AppScreens.kt` — Sealed class defining all nav routes: Creation, Home, Stats, Workout, Charts, Settings, Streak, ChallengeDetail
- `util/GameUtils.kt` — **All mathematical formulas live here**: BMI, body fat % (US Navy method), XP calculations, stat derivation. Do not put formulas elsewhere.
- `util/GameConstants.kt` — Game rules and numeric constants
- `util/AlarmScheduler.kt`, `NotificationScheduler.kt`, `SoundManager.kt` — Side-effect utilities

### `data/`
- `model/GameModels.kt` — Core data classes: `PlayerCharacter` (root state object), `BodyLog`, `WorkoutLog`, `Quest`, `Challenge`, `ExerciseFamily`
- `source/GameStorage.kt` — Jetpack DataStore wrapper; serializes complex objects to JSON via Gson. **When `GameModels.kt` changes, update `GameStorage.kt` to handle the new fields.**
- `repository/GameRepository.kt` — Single source of truth; exposes `playerFlow: Flow<PlayerCharacter>` and suspend functions for all mutations
- `source/` providers — `QuestProvider`, `ItemProvider`, `MilestoneProvider`, `ExerciseDefinitions` (static data definitions)

### `ui/`
- `viewmodel/HomeViewModel.kt` — Central game logic: quest completion, XP/level-up, streak tracking, challenges
- `viewmodel/CreationViewModel.kt` — Character creation wizard state
- `viewmodel/SettingsViewModel.kt` — Settings and reset
- `screens/` — One file per screen; no business logic here, only UI composition
- `components/` — Reusable Compose components (charts, RPG bars, dialogs). Keep these purely presentational.
- `theme/` — Dark Neon RPG design system (colors, typography, Material 3)

**Dependency injection is manual** via `GameViewModelFactory` — no Hilt/Dagger.

## Key Technical Details

- **Min SDK 27** (Android 8.1), **Compile/Target SDK 36**
- **Kotlin 2.0.21**, **Compose BOM 2024.09.00**, **Navigation Compose 2.7.7**
- **Persistence**: DataStore Preferences + Gson 2.10.1. There is no Room database; migration to Room is a planned future step.
- **Charts**: Drawn natively with Compose `Canvas` — no charting library.
- **Navigation**: `MainActivity` owns the `NavHost`. Initial destination is `Creation` if no character exists, otherwise `Home`.
- Character gender is "Guerrero" (male) or "Amazona" (female) — affects the Navy body fat formula.

## Developer Rules (from README)

- **Formulas** (BMI, body fat, stat scaling) belong exclusively in `core/util/GameUtils.kt`.
- **Business logic** must not appear in `ui/` files — only state consumption and composition.
- **Components** in `ui/components/` must remain purely presentational.
- Modifying `GameModels.kt` requires a corresponding update to `GameStorage.kt` for correct Gson serialization.
