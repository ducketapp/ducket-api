package io.ducket.api.extension

fun String.trimWhitespaces() = replace("[\\p{Zs}\\s]+".toRegex(), " ").trim()

fun String.cut(startIndex: Int, cutLength: Int): String {
    val remainingLength = length - startIndex

    if (remainingLength < cutLength) return substring(startIndex, startIndex + remainingLength)
    else if (remainingLength > cutLength) return substring(startIndex, startIndex + cutLength)
    else throw IndexOutOfBoundsException("String index out of range: $startIndex")
}