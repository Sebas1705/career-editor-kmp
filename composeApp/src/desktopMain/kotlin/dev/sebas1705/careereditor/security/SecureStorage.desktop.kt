package dev.sebas1705.careereditor.security

import java.util.prefs.Preferences

actual class SecureStorage {
    // Uses Java Preferences — backed by system keyring on macOS/Linux where available
    private val prefs: Preferences = Preferences.userRoot().node("dev/sebas1705/careereditor")

    actual fun put(key: String, value: String) = prefs.put(key, value)
    actual fun get(key: String): String? = prefs.get(key, null)?.takeIf { it.isNotBlank() }
    actual fun remove(key: String) = prefs.remove(key)
    actual fun clear() = prefs.clear()
}
