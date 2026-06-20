package dev.sebas1705.careereditor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SoftSkill(
    val id: String = "",
    // LocalizedString — { "en": "Adaptability", "es": "Adaptabilidad", [anyCode]: "..." }
    val name: Map<String, String> = emptyMap(),
)
