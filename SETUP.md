# Pixistory setup

## 1. Build
Open the folder in Android Studio, **File > Sync Project with Gradle Files**, pick the `app` run configuration and Run.
Everything except sign-in/sync and the AI works with no setup.

## 2. Sign in with Google and cloud sync (optional)
1. Create a Firebase project and add an Android app with package `com.pixistory`.
2. Add your debug and release **SHA-1** fingerprints to that app (Android Studio: Gradle > app > Tasks > android > signingReport).
3. Authentication > Sign-in method > enable **Google**, copy its **Web client ID**.
4. Create a **Firestore** database and enable **Storage** (photos sync there).
5. Fill `app/src/main/res/values/firebase.xml`: `firebase_app_id`, `firebase_api_key`, `firebase_project_id`, `firebase_storage_bucket`, `firebase_web_client_id`.
6. Firestore rules:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{db}/documents {
    match /users/{uid}/{document=**} { allow read, write: if request.auth != null && request.auth.uid == uid; }
  }
}
```
Storage rules: allow read, write on `users/{uid}/**` when `request.auth.uid == uid`.

## 3. Ask Pixi (AI)
Settings > Ask Pixi: choose Claude or Gemini and paste your own API key. It is stored only on the phone.

## 4. Pixel data
`app/src/main/assets/pixel_knowledge.json` and `pixel_drops.json` hold the history, specs, faults and Pixel Drops (English and Urdu).
`tools/` has the scripts and notes used to build them. Edit the JSON to correct or extend it.
