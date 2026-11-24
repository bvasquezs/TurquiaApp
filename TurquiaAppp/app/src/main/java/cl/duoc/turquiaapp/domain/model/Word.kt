package cl.duoc.turquiaapp.domain.model

/** Palabra del diccionario turco/español. */
data class Word(
    val id: Long = 0,
    val turkish: String,
    val spanish: String,
    val category: String,
    val emoji: String,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false
)
