package cl.duoc.turquiaapp.data.repository

import android.util.Log
import cl.duoc.turquiaapp.data.local.LocalStorage
import cl.duoc.turquiaapp.data.local.SeedData
import cl.duoc.turquiaapp.data.remote.PalabraDto
import cl.duoc.turquiaapp.data.remote.RetrofitClient
import cl.duoc.turquiaapp.domain.model.Category
import cl.duoc.turquiaapp.domain.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class WordRepository(private val storage: LocalStorage) {

    private val api = RetrofitClient.api

    private fun List<Word>.sortedByTurkish() = sortedBy { it.turkish.lowercase() }

    fun observeAll(): Flow<List<Word>> = storage.words.map { it.sortedByTurkish() }

    fun observeByCategory(category: Category): Flow<List<Word>> =
        storage.words.map { words ->
            words.filter { it.category == category.name }
                .sortedWith(compareBy<Word> { it.isCustom }.thenBy { it.id })
        }

    fun observeFavorites(): Flow<List<Word>> =
        storage.words.map { words -> words.filter { it.isFavorite }.sortedByTurkish() }

    suspend fun seedIfEmpty() {
        try {
            val remotas = api.obtenerTodas()
            if (remotas.isNotEmpty()) {
                val actuales = storage.words.first()
                val palabrasMapeadas = remotas.map { dto ->
                    val existente = actuales.find { it.id == dto.id }
                    val catEnum = Category.entries.find { it.name.equals(dto.categoria, ignoreCase = true) }
                    dto.toDomainWord().copy(
                        category = catEnum?.name ?: (dto.categoria ?: Category.entries.first().name),
                        emoji = catEnum?.emoji ?: "🇹🇷",
                        isFavorite = existente?.isFavorite ?: false
                    )
                }
                storage.updateWords { palabrasMapeadas }
                return
            }
        } catch (e: Exception) {
            Log.e("TurquiaRetrofit", "Error conectando al backend: ${e.message}")
        }

        storage.updateWords { current ->
            if (current.isEmpty()) SeedData.words.mapIndexed { i, w -> w.copy(id = i + 1L) } else current
        }
    }

    suspend fun toggleFavorite(word: Word) {
        storage.updateWords { words ->
            words.map { if (it.id == word.id) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    suspend fun addCustomWord(turkish: String, spanish: String, category: Category, emoji: String): Boolean {
        val cleanTurkish = turkish.trim()
        val cleanSpanish = spanish.trim()

        val actuales = storage.words.first()
        val duplicated = actuales.any {
            it.category == category.name && it.turkish.equals(cleanTurkish, ignoreCase = true)
        }
        if (duplicated) return false

        var idGenerado = (actuales.maxOfOrNull { it.id } ?: 0L) + 1L

        try {
            val dtoEnviado = PalabraDto(
                espanol = cleanSpanish,
                turco = cleanTurkish,
                categoria = category.name
            )
            val respuestaServidor = api.crearPalabra(dtoEnviado)
            if (respuestaServidor.id != null) {
                idGenerado = respuestaServidor.id
            }
        } catch (e: Exception) {
            Log.e("TurquiaRetrofit", "Error guardando en API: ${e.message}")
        }

        storage.updateWords { words ->
            words + Word(
                id = idGenerado,
                turkish = cleanTurkish,
                spanish = cleanSpanish,
                category = category.name,
                emoji = emoji.trim().ifEmpty { category.emoji },
                isCustom = true
            )
        }
        return true
    }

    suspend fun delete(word: Word) {
        try {
            api.eliminarPalabra(word.id)
        } catch (e: Exception) {
            Log.e("TurquiaRetrofit", "Error eliminando en API: ${e.message}")
        }
        storage.updateWords { words -> words.filter { it.id != word.id } }
    }
}