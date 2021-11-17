package io.ducket.api.extension

fun String.trimWhitespaces() = replace("[\\p{Zs}\\s]+".toRegex(), " ").trim()