# Pixistory (PixistoryAS)

Android app for collecting and tracking devices, built around Google Pixel history, specs, Pixel Drops and known faults.
English and Urdu interface.

## Status: WORK IN PROGRESS. This snapshot has NOT been built or compiled.

Done so far:
- Gradle project (Kotlin, Compose, Room, Firebase Auth/Firestore/Storage, WorkManager, androidx.window)
- Data layer: Room entities and DAOs, settings, repository, file store, asset tags and label PDF
- Google sign-in + Firestore/Storage sync, AI client (Claude / Gemini), analytics, exports and backup
- Pixel knowledge assets (EN/UR): generations, 39 models (specs, faults), Pixel Drops 2024 to Sept 2026 with per-feature device lists
- Logo, adaptive icon, launch pop-up with copyright, About screen, language helper (English / Urdu)

Still missing before it can build:
- Screens: Devices list/detail/editor, Pixel Drops, Analysis, Ask Pixi, Settings, Shelf, and PixistoryRoot (referenced but not written)
- res/values/strings.xml and res/values-ur/strings.xml for every R.string used
- Not compiled: expect errors to fix on the first build

Setup for sign-in and sync: fill res/values/firebase.xml (see comments in that file).
tools/ holds the scripts and notes used to build the Pixel data assets.
