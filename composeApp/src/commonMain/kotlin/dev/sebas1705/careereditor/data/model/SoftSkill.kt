package dev.sebas1705.careereditor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SoftSkill(
    val id: String = "",
    val icon: String = "",
    val name_en: String = "",
    val name_es: String = ""
)
