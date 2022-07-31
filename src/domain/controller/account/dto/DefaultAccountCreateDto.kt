package dev.ducket.api.domain.controller.account.dto

import dev.ducket.api.app.DEFAULT_SCALE
import dev.ducket.api.utils.scaleBetween
import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotNull
import java.math.BigDecimal

data class DefaultAccountCreateDto(
    val name: String,
    val startBalance: BigDecimal,
) {
    fun validate(): DefaultAccountCreateDto {
        org.valiktor.validate(this) {
            validate(DefaultAccountCreateDto::name).isNotNull().hasSize(1, 64)
            validate(DefaultAccountCreateDto::startBalance).scaleBetween(0, DEFAULT_SCALE)
        }
        return this
    }
}