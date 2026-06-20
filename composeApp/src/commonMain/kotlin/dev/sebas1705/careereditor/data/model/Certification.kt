package dev.sebas1705.careereditor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Certification(
    val id: String = "",
    val name: String = "",
    val issuer: String = "",
    val date: String = "",
    val url: String = ""
)
