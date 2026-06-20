package dev.sebas1705.careereditor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Job(
    val id: String = "",
    val role: LocalizedText = LocalizedText(),
    val company: String = "",
    val companyUrl: String = "",
    val period: LocalizedText = LocalizedText(),
    val type: LocalizedText = LocalizedText(),
    val desc: LocalizedText = LocalizedText(),
    val projects: List<LocalizedText> = emptyList(),
    val achievements: List<LocalizedText> = emptyList()
)

@Serializable
data class LocalizedText(
    val en: String = "",
    val es: String = ""
)
