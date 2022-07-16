package dev.ducket.api.clients.rates.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class HeaderDto(
    @field:JacksonXmlProperty(namespace = "message", localName = "Sender")
    val sender: SenderDto,
)