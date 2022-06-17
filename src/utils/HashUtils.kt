package io.ducket.api.utils


import org.mindrot.jbcrypt.BCrypt

object HashUtils {
    private const val BCRYPT_HASH_ROUNDS = 12

    fun hash(str: String): String = BCrypt.hashpw(str, BCrypt.gensalt(BCRYPT_HASH_ROUNDS))

    fun check(first: String, second: String): Boolean = BCrypt.checkpw(first, second)
}
