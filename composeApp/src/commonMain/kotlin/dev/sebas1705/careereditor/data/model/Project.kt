package dev.sebas1705.careereditor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: String = "",
    val name: String = "",
    val context: String = "",
    // LocalizedString — { "en": "...", "es": "...", [anyCode]: "..." }
    val desc: Map<String, String> = emptyMap(),
    val tags: List<String> = emptyList(),
    val github: String? = null,
    val demo: String? = null
)
