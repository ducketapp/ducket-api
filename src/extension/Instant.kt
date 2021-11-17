package io.ducket.api.extension

import java.time.Instant

fun Instant.isBeforeInclusive(other: Instant): Boolean {
    return this.isBefore(other) || this == other
}

fun Instant.isAfterInclusive(other: Instant): Boolean {
    return this.isAfter(other) || this == other
}