package dev.sebas1705.careereditor.utils

fun String.toSlug(): String = trim()
    .lowercase()
    .replace(Regex("[àáâãäå]"), "a")
    .replace(Regex("[èéêë]"), "e")
    .replace(Regex("[ìíîï]"), "i")
    .replace(Regex("[òóôõö]"), "o")
    .replace(Regex("[ùúûü]"), "u")
    .replace(Regex("[ñ]"), "n")
    .replace(Regex("[ç]"), "c")
    .replace(Regex("[^a-z0-9]+"), "-")
    .trim('-')
