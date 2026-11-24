package cl.duoc.turquiaapp.data.local

import android.content.Context

/** Guarda solo el id del usuario con sesión iniciada (nunca la contraseña). */
class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    var userId: Long
        get() = prefs.getLong(KEY_USER_ID, NO_USER)
        set(value) = prefs.edit().putLong(KEY_USER_ID, value).apply()

    val isLoggedIn: Boolean get() = userId != NO_USER

    fun clear() = prefs.edit().remove(KEY_USER_ID).apply()

    private companion object {
        const val KEY_USER_ID = "user_id"
        const val NO_USER = -1L
    }
}
