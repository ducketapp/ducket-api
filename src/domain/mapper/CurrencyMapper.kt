package dev.ducket.api.domain.mapper

import dev.ducket.api.domain.model.currency.Currency
import dev.ducket.api.domain.controller.currency.dto.CurrencyDto
import dev.ducket.api.domain.controller.currency.dto.CurrencyRateDto
import dev.ducket.api.domain.model.currency.CurrencyRate

object CurrencyMapper {

    fun mapModelToDto(model: CurrencyRate): CurrencyRateDto {
        return DataClassMapper<CurrencyRate, CurrencyRateDto>()
            .register(CurrencyRateDto::baseCurrency, DataClassMapper<Currency, CurrencyDto>())
            .register(CurrencyRateDto::quoteCurrency, DataClassMapper<Currency, CurrencyDto>())
            .invoke(model)
    }

    fun mapModelToDto(model: Currency): CurrencyDto {
        return DataClassMapper<Currency, CurrencyDto>().invoke(model)
    }
}