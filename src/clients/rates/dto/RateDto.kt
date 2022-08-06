package dev.ducketapp.service.clients.rates.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class RateDto(
    @field:JacksonXmlProperty(localName = "TIME_PERIOD", isAttribute = true)
    val date: String,
    @field:JacksonXmlProperty(localName = "OBS_VALUE", isAttribute = true)
    val value: String,
)
