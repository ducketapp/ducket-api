package io.ducket.api.utils

import java.time.Instant

object TransferCodeGenerator {

    fun generate(): String {
        val ts = Instant.now().toEpochMilli()
        var out = ""
        for (i in 0..ts.toString().length step 2) {
            out += Integer.valueOf(ts.toString().cut(startIndex = i, cutLength = 2)).toString(radix = 36)
        }

        return "TR_${out.uppercase()}"
    }
}