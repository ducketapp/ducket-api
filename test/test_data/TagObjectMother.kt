package org.expenny.service.test_data

import org.expenny.service.domain.model.tag.Tag
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