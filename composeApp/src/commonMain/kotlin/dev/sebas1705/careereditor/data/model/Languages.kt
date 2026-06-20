package dev.sebas1705.careereditor.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Language(
    val code: String = "",
    val label: String = "",
    @SerialName("label_native") val labelNative: String = ""
)

@Serializable
data class Languages(
    val default: String = "en",
    val supported: List<Language> = listOf(
        Language("en", "English", "English"),
        Language("es", "Spanish", "Español")
    )
)

/** Helper: resolve a localized value, falling back to default lang then first available. */
fun Map<String, String>.resolve(code: String, fallback: String = "en"): String =
    this[code] ?: this[fallback] ?: values.firstOrNull() ?: ""

fun Map<String, List<String>>.resolveList(code: String, fallback: String = "en"): List<String> =
    this[code] ?: this[fallback] ?: values.firstOrNull() ?: emptyList()
