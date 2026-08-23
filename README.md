# career-editor-kmp

KMP desktop and Android editor for managing data in [career-api](https://api.sebas1705.dev) — the personal career portfolio API.

**Platforms:** Android · Desktop (JVM)

---

## Stack

- **Kotlin Multiplatform** — shared business logic across platforms
- **Compose Multiplatform** — shared UI for Android and Desktop
- **Ktor Client** — HTTP communication with career-api
- **Clean Architecture** — domain / data / presentation separation

---

## Structure

```
composeApp/
├── androidMain/     # Android entry point
├── desktopMain/     # Desktop entry point
└── commonMain/      # Shared UI, logic and network layer
```

---

## Getting started

```bash
git clone https://github.com/Sebas1705Carreer/career-editor-kmp.git
cd career-editor-kmp
```

**Android:** open in Android Studio and run the `composeApp` configuration.

**Desktop:**
```bash
./gradlew desktopRun
```

> Requires Java 17+ and Android Studio Hedgehog or later.

---

## Related

- [career-api-worker](https://github.com/Sebas1705Carreer/career-api-worker) — backend API (Cloudflare Workers + KV)
## Publishing a release

One tag produces every installer — Windows, macOS, Linux and Android — attached to a single GitHub release. The repository is private, so those downloads are private too, which is why no separate distribution service is involved.

### One-time setup: the Android keystore

Android refuses to install an unsigned release build, and it refuses to *update* an app signed with a different key. Create the keystore once and keep it safe:

```bash
keytool -genkeypair -v -keystore folio.jks -alias folio \
  -keyalg RSA -keysize 2048 -validity 10000
```

**Back up `folio.jks` somewhere you will still have it in five years.** If it is lost, the only way to ship an update is to uninstall the app and lose its local data — Android has no recovery path for this.

Then turn it into a repository secret:

```bash
base64 -w0 folio.jks > folio.jks.base64
```

Add four secrets under **Settings → Secrets and variables → Actions**:

| Secret | Value |
|---|---|
| `ANDROID_KEYSTORE_BASE64` | contents of `folio.jks.base64` |
| `ANDROID_KEYSTORE_PASSWORD` | the store password chosen above |
| `ANDROID_KEY_ALIAS` | `folio` |
| `ANDROID_KEY_PASSWORD` | the key password chosen above |

Delete `folio.jks.base64` afterwards. Neither the keystore nor any password belongs in this repository — the build reads them from the environment, and a build without them simply produces an unsigned APK.

### Cutting a release

Version numbers live in **two** places in `composeApp/build.gradle.kts` and both have to move together: `versionName` / `versionCode` in the `android` block, and `packageVersion` under `compose.desktop`. A mismatch ships installers that disagree about what they are.

```bash
git tag v1.2.0
git push origin v1.2.0
```

The workflow builds four artifacts in parallel and publishes them. Installing on Android means downloading the APK and allowing installation from the browser once — that prompt is the price of not using a store, and it appears only the first time.

### Building locally

```bash
./gradlew :composeApp:packageDistributionForCurrentOS   # installer for this OS
./gradlew :composeApp:assembleDebug                     # Android debug APK
```
