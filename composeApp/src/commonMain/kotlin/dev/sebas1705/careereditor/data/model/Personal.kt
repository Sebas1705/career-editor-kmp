package dev.sebas1705.careereditor.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Personal(
    val name: String = "",
    val email: String = "",
    @SerialName("cv_url") val cvUrl: String = "",
    val github: String = "",
    val linkedin: String = "",
    val codewars: String = "",
    // LocalizedString fields — { "en": "...", "es": "...", [anyCode]: "..." }
    val greeting: Map<String, String> = emptyMap(),
    val role: Map<String, String> = emptyMap(),
    val tagline: Map<String, String> = emptyMap(),
    val bio: Map<String, String> = emptyMap(),
    val location: Map<String, String> = emptyMap(),
)
