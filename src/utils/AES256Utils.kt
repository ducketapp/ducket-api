package io.ducket.api.utils

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AES256Utils {
    private val encorder = Base64.getEncoder()
    private val decorder = Base64.getDecoder()

    private fun cipher(opmode: Int, secretKey: String): Cipher {
        if (secretKey.length != 32) throw RuntimeException("SecretKey length is not 32 chars")

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")
        val ivParameterSpec = IvParameterSpec(secretKey.substring(0, 16).toByteArray(Charsets.UTF_8))
        cipher.init(opmode, secretKeySpec, ivParameterSpec)

        return cipher
    }

    fun encrypt(str: String, secretKey: String): String {
        val encrypted = cipher(Cipher.ENCRYPT_MODE, secretKey).doFinal(str.toByteArray(Charsets.UTF_8))
        return String(encorder.encode(encrypted))
    }

    fun decrypt(str: String, secretKey: String): String {
        val byteStr = decorder.decode(str.toByteArray(Charsets.UTF_8))
        return String(cipher(Cipher.DECRYPT_MODE, secretKey).doFinal(byteStr))
    }
}