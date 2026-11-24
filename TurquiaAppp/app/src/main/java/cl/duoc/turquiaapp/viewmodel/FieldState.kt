package cl.duoc.turquiaapp.viewmodel

/** Estado de un campo de formulario: su valor y el error de validación (null = sin error). */
data class FieldState(val value: String = "", val error: String? = null) {
    /** Se considera válido (ícono ✓) cuando tiene contenido y ningún error. */
    val isValid: Boolean get() = value.isNotEmpty() && error == null
}
