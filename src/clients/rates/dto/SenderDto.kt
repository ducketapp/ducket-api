package dev.ducketapp.service.clients.rates.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SenderDto(
    @field:JacksonXmlProperty(localName = "id", isAttribute = true)
    val id: String,
)
