package cl.duoc.turquiaapp.data.remote

import cl.duoc.turquiaapp.domain.model.Word

data class PalabraDto(
    val id: Long? = null,
    val espanol: String,
    val turco: String,
    val categoria: String,
    val usuario: String? = null
) {
    fun toDomainWord(): Word {
        return Word(
            id = id ?: 0L,
            turkish = turco,
            spanish = espanol,
            category = categoria,
            emoji = "🇹🇷",
            isCustom = true
        )
    }
}