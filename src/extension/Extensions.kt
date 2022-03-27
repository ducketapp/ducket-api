package io.ducket.api.extension

import java.math.BigDecimal
import java.time.Instant
import kotlin.reflect.full.declaredMemberProperties

fun String.trimWhitespaces() = replace("[\\p{Zs}\\s]+".toRegex(), " ").trim()

fun String.cut(startIndex: Int, cutLength: Int): String {
    val remainingLength = length - startIndex

    if (remainingLength < cutLength) return substring(startIndex, startIndex + remainingLength)
    else if (remainingLength > cutLength) return substring(startIndex, startIndex + cutLength)
    else throw IndexOutOfBoundsException("String index out of range: $startIndex")
}

inline fun <T> Iterable<T>.sumByDecimal(selector: (T) -> BigDecimal): BigDecimal {
    var sum = BigDecimal(0)
    for (element in this) sum += selector(element)
    return sum
}

fun Instant.isBeforeInclusive(other: Instant): Boolean {
    return this.isBefore(other) || this == other
}

fun Instant.isAfterInclusive(other: Instant): Boolean {
    return this.isAfter(other) || this == other
}

fun Any.declaredMemberPropertiesNull(): Boolean {
    if (this::class.declaredMemberProperties.any { !it.returnType.isMarkedNullable }) return false
    return this::class.declaredMemberProperties.none { it.getter.call(this) != null }
}

fun BigDecimal.gt(that: BigDecimal): Boolean {
    return this.compareTo(that) > 0
}

fun BigDecimal.lt(that: BigDecimal): Boolean {
    return this.compareTo(that) < 0
}

fun BigDecimal.eq(that: BigDecimal): Boolean {
    return this.compareTo(that) == 0
}