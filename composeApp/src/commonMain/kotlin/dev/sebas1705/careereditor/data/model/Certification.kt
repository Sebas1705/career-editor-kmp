package dev.sebas1705.careereditor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Certification(
    val id: String = "",
    val name: String = "",
    val issuer: String = "",
    val date: String = "",
    // Nullable: la API sirve url null en certificaciones "in progress"
    val url: String? = null
)
