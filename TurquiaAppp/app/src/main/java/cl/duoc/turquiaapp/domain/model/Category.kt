package cl.duoc.turquiaapp.domain.model

/** Categorías de vocabulario que ve el niño en la pantalla de inicio. */
enum class Category(val label: String, val emoji: String) {
    SALUDOS("Saludos", "👋"),
    ANIMALES("Animales", "🐾"),
    COLORES("Colores", "🎨"),
    NUMEROS("Números", "🔢"),
    FAMILIA("Familia", "👨‍👩‍👧"),
    COMIDA("Comida", "🍎");

    companion object {
        fun fromKey(key: String?): Category? = entries.firstOrNull { it.name == key }
    }
}
