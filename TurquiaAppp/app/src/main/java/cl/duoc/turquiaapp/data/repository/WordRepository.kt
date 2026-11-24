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

    /** Obtiene el nombre del perfil activo desde storage.users */
    private suspend fun getActiveProfileName(): String {
        val usuarios = storage.users.first()
        return usuarios.lastOrNull()?.name?.trim().orEmpty()
    }

    fun observeAll(): Flow<List<Word>> = storage.words.map { it.sortedByTurkish() }

    fun observeByCategory(category: Category): Flow<List<Word>> =
        storage.words.map { words ->
            words.filter { it.category == category.name }
                .sortedWith(compareBy<Word> { it.isCustom }.thenBy { it.id })
        }

    fun observeFavorites(): Flow<List<Word>> =
        storage.words.map { words -> words.filter { it.isFavorite }.sortedByTurkish() }

    suspend fun seedIfEmpty() {
        val actuales = storage.words.first()
        val perfilActivo = getActiveProfileName()

        // 1. Siempre preparamos las palabras predeterminadas (IDs negativos para no chocar con MySQL)
        val predeterminadas = SeedData.words.mapIndexed { i, w ->
            val idBase = -(i + 1L)
            val existente = actuales.find { it.id == idBase }
            w.copy(
                id = idBase,
                isCustom = false,
                isFavorite = existente?.isFavorite ?: false
            )
        }

        // 2. Traemos las palabras personalizadas desde Spring Boot / MySQL filtradas por perfil
        var personalizadas: List<Word> = actuales.filter { it.isCustom }

        try {
            val remotas = api.obtenerTodas()
            val remotasDelPerfil = if (perfilActivo.isNotEmpty()) {
                remotas.filter { it.usuario.isNullOrBlank() || it.usuario.equals(perfilActivo, ignoreCase = true) }
            } else {
                remotas
            }

            personalizadas = remotasDelPerfil.map { dto ->
                val existente = actuales.find { it.id == dto.id }
                val catEnum = Category.entries.find { it.name.equals(dto.categoria, ignoreCase = true) }
                dto.toDomainWord().copy(
                    category = catEnum?.name ?: dto.categoria,
                    emoji = catEnum?.emoji ?: "🇹🇷",
                    isCustom = true,
                    isFavorite = existente?.isFavorite ?: false
                )
            }
        } catch (e: Exception) {
            Log.e("TurquiaRetrofit", "Error conectando al backend: ${e.message}")
        }

        // 3. Combinamos las predeterminadas + las personalizadas del perfil en MySQL
        storage.updateWords { predeterminadas + personalizadas }
    }

    suspend fun toggleFavorite(word: Word) {
        storage.updateWords { words ->
            words.map { if (it.id == word.id) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    suspend fun addCustomWord(turkish: String, spanish: String, category: Category, emoji: String): Boolean {
        val cleanTurkish = turkish.trim()
        val cleanSpanish = spanish.trim()
        val perfilActivo = getActiveProfileName()

        val actuales = storage.words.first()
        val duplicated = actuales.any {
            it.category == category.name && it.turkish.equals(cleanTurkish, ignoreCase = true)
        }
        if (duplicated) return false

        var idGenerado = (actuales.filter { it.id > 0 }.maxOfOrNull { it.id } ?: 0L) + 1L

        try {
            val dtoEnviado = PalabraDto(
                espanol = cleanSpanish,
                turco = cleanTurkish,
                categoria = category.name,
                usuario = perfilActivo.ifEmpty { null }
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
        if (!word.isCustom) return
        try {
            api.eliminarPalabra(word.id)
        } catch (e: Exception) {
            Log.e("TurquiaRetrofit", "Error eliminando en API: ${e.message}")
        }
        storage.updateWords { words -> words.filter { it.id != word.id } }
    }
}