package dev.ducket.api.utils

import dev.ducket.api.app.BCRYPT_HASH_ROUNDS
import org.mindrot.jbcrypt.BCrypt

object HashUtils {
    fun hash(str: String): String = BCrypt.hashpw(str, BCrypt.gensalt(BCRYPT_HASH_ROUNDS))
    fun check(str: String, hash: String): Boolean = BCrypt.checkpw(str, hash)
}
