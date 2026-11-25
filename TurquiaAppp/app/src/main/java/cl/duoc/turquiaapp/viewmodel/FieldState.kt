package cl.duoc.turquiaapp.viewmodel

data class FieldState(val value: String = "", val error: String? = null) {
    val isValid: Boolean get() = value.isNotEmpty() && error == null
}
