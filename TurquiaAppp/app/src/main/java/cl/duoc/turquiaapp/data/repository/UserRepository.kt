package cl.duoc.turquiaapp.data.repository

import cl.duoc.turquiaapp.data.local.LocalStorage
import cl.duoc.turquiaapp.data.local.SessionManager
import cl.duoc.turquiaapp.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

sealed interface AuthResult {
    data object Success : AuthResult
    data class Error(val message: String) : AuthResult
}

class UserRepository(
    private val storage: LocalStorage,
    private val session: SessionManager
) {
    val isLoggedIn: Boolean get() = session.isLoggedIn

    fun observeCurrentUser(): Flow<User?> =
        storage.users.map { users -> users.firstOrNull { it.id == session.userId } }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        childName: String,
        childAge: Int
    ): AuthResult {
        val normalizedEmail = email.trim().lowercase()
        val salt = PasswordHasher.newSalt()
        var duplicated = false
        var newId = 0L
        // La comprobación de correo repetido y el guardado ocurren en la misma operación atómica.
        storage.updateUsers { users ->
            if (users.any { it.email == normalizedEmail }) {
                duplicated = true
                users
            } else {
                newId = (users.maxOfOrNull { it.id } ?: 0L) + 1L
                users + User(
                    id = newId,
                    name = name.trim(),
                    email = normalizedEmail,
                    passwordHash = PasswordHasher.hash(password, salt),
                    salt = salt,
                    childName = childName.trim(),
                    childAge = childAge
                )
            }
        }
        if (duplicated) return AuthResult.Error("Este correo ya está registrado")
        session.userId = newId
        return AuthResult.Success
    }

    suspend fun login(email: String, password: String): AuthResult {
        val normalizedEmail = email.trim().lowercase()
        val user = storage.users.first().firstOrNull { it.email == normalizedEmail }
        // Mismo mensaje para correo inexistente o clave incorrecta (no filtra qué correos existen).
        if (user == null || user.passwordHash != PasswordHasher.hash(password, user.salt)) {
            return AuthResult.Error("Correo o contraseña incorrectos")
        }
        session.userId = user.id
        return AuthResult.Success
    }

    suspend fun updateProfile(userId: Long, childName: String, childAge: Int, photoPath: String?) {
        storage.updateUsers { users ->
            users.map {
                if (it.id == userId) it.copy(childName = childName.trim(), childAge = childAge, photoPath = photoPath)
                else it
            }
        }
    }

    fun logout() = session.clear()
}
