package io.budgery.api.domain.controller.budget

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class BudgetPeriod {
    MONTHLY, WEEKLY, ANNUAL;

    companion object {
        fun getPeriod(start: LocalDate, end: LocalDate): BudgetPeriod? {
            val daysDiff = (ChronoUnit.DAYS.between(start, end) + 1).toInt();

            if (start.month == end.month) {
                if (daysDiff == 7) {
                    return WEEKLY
                } else if (daysDiff == start.lengthOfMonth()) {
                    return MONTHLY
                }
            }

            if (start.year == end.year && start.lengthOfYear() == daysDiff) {
                return ANNUAL
            }

            return null
        }
    }

    fun getBounds(): Pair<LocalDate, LocalDate> {
        val now: LocalDate = LocalDate.now()
        val startDay: LocalDate
        val endDay: LocalDate

        when (this) {
            MONTHLY -> {
                startDay = now.withDayOfMonth(1)
                endDay = now.withDayOfMonth(now.lengthOfMonth())
            }
            WEEKLY -> {
                startDay = now.with(DayOfWeek.MONDAY)
                endDay = now.with(DayOfWeek.SUNDAY)
            }
            ANNUAL -> {
                startDay = now.withDayOfYear(1)
                endDay = now.withDayOfYear(now.lengthOfYear())
            }
        }

        return Pair(startDay, endDay)
    }
}