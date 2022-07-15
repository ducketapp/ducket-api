package io.ducket.api.utils

import org.valiktor.Constraint
import org.valiktor.Validator
import java.math.BigDecimal

data class BigDecimalScaleRange(val start: Int, val end: Int) : Constraint

data class Length(val size: Int) : Constraint

fun <E> Validator<E>.Property<BigDecimal?>.scaleBetween(start: Int, end: Int) =
    this.validate(BigDecimalScaleRange(start, end)) { it == null || it.scale() in start.rangeTo(end) }

fun <E> Validator<E>.Property<String?>.hasLength(length: Int = Int.MAX_VALUE): Validator<E>.Property<String?> =
    this.validate(Length(length)) { it == null || it.length == length }