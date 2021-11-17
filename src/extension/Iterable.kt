package io.ducket.api.extension

import java.math.BigDecimal

inline fun <T> Iterable<T>.sumByDecimal(selector: (T) -> BigDecimal): BigDecimal {
    var sum = BigDecimal(0)
    for (element in this) sum += selector(element)
    return sum
}