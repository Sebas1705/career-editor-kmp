# career-editor-kmp

KMP desktop and Android editor for managing data in [career-api](https://career-api.sebas1705.workers.dev) — the personal career portfolio API.

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