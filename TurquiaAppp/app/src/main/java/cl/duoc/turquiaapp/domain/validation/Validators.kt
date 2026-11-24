package cl.duoc.turquiaapp.domain.validation

import cl.duoc.turquiaapp.domain.model.Category

/**
 * Reglas de validación de TODOS los formularios de la app.
 *
 * Es Kotlin puro (sin Android ni Compose): la UI solo muestra el mensaje que
 * devuelve cada función. Retornan `null` cuando el valor es válido.
 */
object Validators {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun personName(value: String): String? {
        val v = value.trim()
        return when {
            v.isEmpty() -> "Ingresa un nombre"
            v.length < 2 -> "Mínimo 2 caracteres"
            v.length > 40 -> "Máximo 40 caracteres"
            !v.all { it.isLetter() || it == ' ' } -> "Solo letras y espacios"
            else -> null
        }
    }

    fun email(value: String): String? {
        val v = value.trim()
        return when {
            v.isEmpty() -> "Ingresa tu correo"
            !EMAIL_REGEX.matches(v) -> "Correo no válido (ej: nombre@correo.cl)"
            else -> null
        }
    }

    fun password(value: String): String? = when {
        value.isEmpty() -> "Ingresa una contraseña"
        value.length < 8 -> "Mínimo 8 caracteres"
        value.none { it.isDigit() } -> "Incluye al menos un número"
        value.none { it.isUpperCase() } -> "Incluye al menos una mayúscula"
        else -> null
    }

    /** Para el login solo se exige que no esté vacía (la regla fuerte aplica al registrarse). */
    fun loginPassword(value: String): String? =
        if (value.isEmpty()) "Ingresa tu contraseña" else null

    fun confirmPassword(password: String, confirm: String): String? = when {
        confirm.isEmpty() -> "Repite la contraseña"
        confirm != password -> "Las contraseñas no coinciden"
        else -> null
    }

    fun childAge(value: String): String? {
        val age = value.trim().toIntOrNull()
        return when {
            value.isBlank() -> "Ingresa la edad"
            age == null -> "Debe ser un número"
            age !in 3..12 -> "La edad debe estar entre 3 y 12 años"
            else -> null
        }
    }

    /** Palabra o frase corta (turco o español). Permite letras, espacios, guion y apóstrofe. */
    fun word(value: String, fieldName: String): String? {
        val v = value.trim()
        return when {
            v.isEmpty() -> "Ingresa $fieldName"
            v.length > 40 -> "Máximo 40 caracteres"
            !v.all { it.isLetter() || it == ' ' || it == '-' || it == '\'' } ->
                "Solo letras, espacios y guiones"
            else -> null
        }
    }

    fun category(value: Category?): String? =
        if (value == null) "Elige una categoría" else null

    /** Texto del traductor: es opcional (vacío = sin búsqueda) pero si hay texto debe ser válido. */
    fun searchText(value: String): String? = when {
        value.isEmpty() -> null
        value.length > 40 -> "Máximo 40 caracteres"
        !value.all { it.isLetter() || it == ' ' || it == '-' || it == '\'' } ->
            "Solo letras y espacios"
        else -> null
    }
}
