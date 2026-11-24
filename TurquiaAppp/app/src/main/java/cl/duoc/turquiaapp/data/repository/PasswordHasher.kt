package cl.duoc.turquiaapp.data.repository

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

/** Las contraseñas se guardan con sal aleatoria + SHA-256, nunca en texto plano. */
object PasswordHasher {
    fun newSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun hash(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest((salt + password).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
