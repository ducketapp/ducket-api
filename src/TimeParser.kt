package io.ducket.api

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.time.*
import java.time.format.DateTimeFormatter

class InstantDeserializer : StdDeserializer<Instant>(Instant::class.java) {

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Instant {
        return Instant.parse(p!!.text)
    }
}

class LocalDateDeserializer : StdDeserializer<LocalDate>(LocalDate::class.java) {

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDate {
        return LocalDate.parse(p!!.text, DateTimeFormatter.ISO_LOCAL_DATE)
    }
}

class InstantSerializer : StdSerializer<Instant>(Instant::class.java) {

    override fun serialize(value: Instant?, gen: JsonGenerator?, provider: SerializerProvider?) {
        gen?.writeString(DateTimeFormatter.ISO_INSTANT.format(value))
        // gen?.writeString(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value?.atZone(ZoneId.of("Europe/Warsaw"))))
    }
}

class LocalDateSerializer : StdSerializer<LocalDate>(LocalDate::class.java) {

    override fun serialize(value: LocalDate?, gen: JsonGenerator?, provider: SerializerProvider?) {
        gen?.writeString(value?.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }
}

class LocalTimeSerializer : StdSerializer<LocalTime>(LocalTime::class.java) {

    override fun serialize(value: LocalTime?, gen: JsonGenerator?, provider: SerializerProvider?) {
        gen?.writeString(value?.format(DateTimeFormatter.ISO_LOCAL_TIME))
    }
}