package dev.sebas1705.careereditor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: String = "",
    val name: String = "",
    val context: String = "",
    val desc: LocalizedText = LocalizedText(),
    val long_desc: LocalizedText = LocalizedText(),
    val tags: List<String> = emptyList(),
    val github: String? = null,
    val demo: String? = null
)
