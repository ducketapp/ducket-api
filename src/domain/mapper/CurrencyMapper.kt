package dev.ducketapp.service.domain.mapper

import dev.ducketapp.service.domain.model.currency.Currency
import dev.ducketapp.service.domain.controller.currency.dto.CurrencyDto
import dev.ducketapp.service.domain.controller.currency.dto.CurrencyRateDto
import dev.ducketapp.service.domain.model.currency.CurrencyRate

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