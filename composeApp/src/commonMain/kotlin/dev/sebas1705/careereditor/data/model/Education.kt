package dev.sebas1705.careereditor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Education(
    val id: String = "",
    val degree: LocalizedText = LocalizedText(),
    val school: String = "",
    val period: LocalizedText = LocalizedText(),
    val detail: LocalizedText = LocalizedText(),
    val icon: String = ""
)
