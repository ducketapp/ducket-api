package dev.ducketapp.service

import dev.ducketapp.service.test_data.UserObjectMother
import io.mockk.clearAllMocks
import org.junit.jupiter.api.BeforeEach

abstract class BaseUnitTest {
    protected val userId = UserObjectMother.user().id

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
    }
}