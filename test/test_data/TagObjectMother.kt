package io.ducket.api.test_data

import io.ducket.api.domain.model.tag.Tag
import java.time.Instant

class TagObjectMother {
    companion object {
        fun tag() = Tag(
            id = 1,
            user = UserObjectMother.user(),
            title = "Tag title",
            createdAt = Instant.ofEpochSecond(1642708900),
            modifiedAt = Instant.ofEpochSecond(1642708900),
        )
    }
}