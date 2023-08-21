package org.expenny.service.domain.mapper

import org.expenny.service.domain.model.currency.Currency
import org.expenny.service.domain.controller.currency.dto.CurrencyDto
import org.expenny.service.domain.controller.currency.dto.CurrencyRateDto
import org.expenny.service.domain.model.currency.CurrencyRate

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