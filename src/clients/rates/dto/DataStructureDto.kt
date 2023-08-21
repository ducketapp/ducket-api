package org.expenny.service.clients.rates.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(namespace = "message", localName = "StructureSpecificData")
data class DataStructureDto(
    @field:JacksonXmlProperty(namespace = "message", localName = "DataSet")
    val dataSet: DataSetDto,
    @field:JacksonXmlProperty(namespace = "message", localName = "Header")
    val header: HeaderDto,
)
