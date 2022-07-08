package domain.mapper

import domain.model.currency.Currency
import io.ducket.api.domain.controller.currency.dto.CurrencyDto
import io.ducket.api.domain.controller.currency.dto.CurrencyRateDto
import io.ducket.api.domain.model.currency.CurrencyRate

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