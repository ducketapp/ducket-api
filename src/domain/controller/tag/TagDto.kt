package io.ducket.api.domain.controller.tag

import io.ducket.api.domain.model.tag.Tag

data class TagDto(
    val id: Long,
    val name: String,
) {
    constructor(tag: Tag) : this(
        id = tag.id,
        name = tag.name,
    )
}
