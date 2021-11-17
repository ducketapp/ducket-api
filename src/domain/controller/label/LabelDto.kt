package io.ducket.api.domain.controller.label

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.InstantSerializer
import io.ducket.api.domain.model.label.Label
import java.time.Instant
import java.time.LocalDateTime

class LabelDto(@JsonIgnore val label: Label) {
    val id: String = label.id.toString()
    val name: String = label.name
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant = label.createdAt
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant = label.modifiedAt
}