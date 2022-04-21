package io.ducket.api.utils

import org.valiktor.Constraint
import org.valiktor.Validator
import org.valiktor.constraints.Less
import org.valiktor.functions.isLessThan
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class BigDecimalScaleRange(val start: Int, val end: Int) : Constraint
data class BigDecimalScale(val scale: Int) : Constraint

fun <E> Validator<E>.Property<BigDecimal?>.scaleBetween(start: Int, end: Int) =
    this.validate(BigDecimalScaleRange(start, end)) { it == null || it.scale() in start.rangeTo(end) }

fun <E> Validator<E>.Property<BigDecimal?>.scale(scale: Int) =
    this.validate(BigDecimalScale(scale)) { it == null || it.scale() == scale }

fun <E> Validator<E>.Property<Instant?>.isLessThan(value: Instant, tz: TimeZone) =
    this.validate(Less(value)) { it == null || it.atZone(tz.toZoneId()).toInstant() < value }

