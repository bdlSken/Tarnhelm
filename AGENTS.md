# AGENTS.md

## Cursor Cloud specific instructions

Tarnhelm is a **native Android app** (Kotlin + Gradle) that also ships an Xposed/LSPosed
module for cleaning tracking parameters out of sharing links. There is no backend/server —
"running" the project means building the Android APK.

### Toolchain (already provisioned by the update script + VM snapshot)
- JDK 21 is used to run Gradle (bytecode target is Java 17 via `compileOptions`); this is fine.
- Gradle is driven through the wrapper: `./gradlew` (Gradle 8.11.1, AGP 8.9.3).
- Android SDK lives at `$HOME/android-sdk` (`ANDROID_HOME` is exported in `~/.bashrc`).
  Installed: `platform-tools`, `platforms;android-36` (compileSdk/targetSdk 36), `build-tools;36.0.0`.
- `local.properties` (gitignored) points Gradle at the SDK via `sdk.dir`. The update script
  recreates it if missing, so you normally don't need to touch it.

### Build / test / lint (github flavor is the default one to use)
- Build debug APK: `./gradlew :app:assembleGithubDebug`
  → output at `app/build/outputs/apk/github/debug/app-github-debug.apk`.
- Unit tests: `./gradlew :app:testGithubDebugUnitTest`.
- Lint: `./gradlew :app:lintGithubDebug`. NOTE: lint currently reports ~31 pre-existing
  errors (mostly `MissingTranslation`), so this task fails by design on a clean checkout.
  Treat those as the repo's existing state, not a regression you introduced.
- Product flavors: `github`, `google`, `fdroid`, `coolapk` (use `github` for local dev).

### Emulator / interactive UI
- This VM has **no KVM / hardware virtualization** (`/dev/kvm` is absent), so the Android
  emulator cannot run. UI/runtime testing must be done on real hardware; in the cloud VM the
  best available verification is a successful build + unit tests + APK inspection
  (`apkanalyzer apk summary <apk>`).
