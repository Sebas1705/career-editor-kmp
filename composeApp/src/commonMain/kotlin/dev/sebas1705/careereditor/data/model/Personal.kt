package dev.sebas1705.careereditor.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Personal(
    val name: String = "",
    @SerialName("greeting_en") val greetingEn: String = "",
    @SerialName("greeting_es") val greetingEs: String = "",
    @SerialName("role_en") val roleEn: String = "",
    @SerialName("role_es") val roleEs: String = "",
    @SerialName("tagline_en") val taglineEn: String = "",
    @SerialName("tagline_es") val taglineEs: String = "",
    @SerialName("bio_en") val bioEn: String = "",
    @SerialName("bio_es") val bioEs: String = "",
    val email: String = "",
    @SerialName("location_en") val locationEn: String = "",
    @SerialName("location_es") val locationEs: String = "",
    @SerialName("cv_url") val cvUrl: String = "",
    val github: String = "",
    val linkedin: String = "",
    val codewars: String = ""
)
