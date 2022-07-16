package dev.ducket.api.clients.rates.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReferenceDto(
    @field:JacksonXmlProperty(localName = "Obs")
    @field:JacksonXmlElementWrapper(useWrapping = false)
    val rates: List<RateDto>,
    @field:JacksonXmlProperty(localName = "CURRENCY", isAttribute = true)
    val currency: String,
    @field:JacksonXmlProperty(localName = "CURRENCY_DENOM", isAttribute = true)
    val baseCurrency: String,
)
