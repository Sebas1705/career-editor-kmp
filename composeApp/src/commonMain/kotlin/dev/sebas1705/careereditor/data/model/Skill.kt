package dev.sebas1705.careereditor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Skill(
    val id: String = "",
    val name: String = "",
    val icon_url: String? = null,
    val level: Int = 1,
    val category: String = ""
)
