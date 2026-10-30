# Pixistory (PixistoryAS)

Android app for collecting and tracking devices, built around Google Pixel history, specs, Pixel Drops and known faults.
English and Urdu interface.

## Status
All screens, English and Urdu strings and the Pixel data are written. **The project has not been compiled yet**; all 41 Kotlin files pass a syntax check, but expect some type or API errors on the first build. Send the first Build-tab error and it can be fixed.

Includes: Devices (list, detail, editor, catalogue, barcode tags), Pixel Drops history with exact compatibility, Pixel history/specs/new features/known faults for every Pixel you add, Analysis and Android coverage, Ask Pixi (Claude or Gemini), Google sign-in + Firestore sync, Shelf mode (tabletop posture on Pixel Fold), launch pop-up with logo and copyright, About, English / Urdu.

See SETUP.md for Firebase and AI keys.

## Open in Android Studio
1. File > Open > this folder, then **File > Sync Project with Gradle Files**.
2. The shared run configuration `app` (in `.run/app.run.xml`, module `Pixistory.app`) appears in the toolbar; pick a device and press Run.
3. If the toolbar still says *Add Configuration*, Gradle sync has not finished: open the Build tab and fix the first error shown.
