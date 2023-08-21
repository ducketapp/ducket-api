package org.expenny.service.utils

import org.expenny.service.app.PeriodicBudgetType
import org.threeten.extra.LocalDateRange
import org.threeten.extra.YearQuarter
import java.math.BigDecimal
import java.time.*
import java.time.temporal.IsoFields
import java.time.temporal.TemporalAdjusters

fun String.trimWhitespaces() = replace("[\\p{Zs}\\s]+".toRegex(), " ").trim()

fun <T> List<T>.hasDuplicates(): Boolean {
    return size != hashSetOf(this).size
}

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

fun Instant.isBeforeInclusive(other: Instant?): Boolean {
    return other != null && this.isBefore(other) || this == other
}

fun Instant.isAfterInclusive(other: Instant?): Boolean {
    return other != null && this.isAfter(other) || this == other
}

fun Instant.toLocalDate(): LocalDate {
    return this.atZone(ZoneId.systemDefault()).toLocalDate()
}

fun LocalDate.getPeriodDateRange(periodType: PeriodicBudgetType): LocalDateRange {
    return when(periodType) {
        PeriodicBudgetType.DAILY -> {
            LocalDateRange.of(this, this)
        }
        PeriodicBudgetType.WEEKLY -> {
            LocalDateRange.of(with(DayOfWeek.MONDAY), with(DayOfWeek.SUNDAY))
        }
        PeriodicBudgetType.MONTHLY -> {
            LocalDateRange.of(with(TemporalAdjusters.firstDayOfMonth()), with(TemporalAdjusters.lastDayOfMonth()))
        }
        PeriodicBudgetType.QUARTERLY -> {
            LocalDateRange.of(
                YearQuarter.of(year, get(IsoFields.QUARTER_OF_YEAR)).atDay(1),
                YearQuarter.of(year, get(IsoFields.QUARTER_OF_YEAR)).atEndOfQuarter(),
            )
        }
        PeriodicBudgetType.ANNUALLY -> {
            LocalDateRange.of(with(TemporalAdjusters.firstDayOfYear()), with(TemporalAdjusters.lastDayOfYear()))
        }
    }
}