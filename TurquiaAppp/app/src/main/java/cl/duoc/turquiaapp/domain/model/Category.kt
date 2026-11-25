package cl.duoc.turquiaapp.domain.model

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
