package cl.duoc.turquiaapp.data.repository

import cl.duoc.turquiaapp.data.local.LocalStorage
import cl.duoc.turquiaapp.data.local.SeedData
import cl.duoc.turquiaapp.domain.model.Category
import cl.duoc.turquiaapp.domain.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WordRepository(private val storage: LocalStorage) {

    private fun List<Word>.sortedByTurkish() = sortedBy { it.turkish.lowercase() }

    fun observeAll(): Flow<List<Word>> = storage.words.map { it.sortedByTurkish() }

    fun observeByCategory(category: Category): Flow<List<Word>> =
        storage.words.map { words ->
            words.filter { it.category == category.name }
                .sortedWith(compareBy<Word> { it.isCustom }.thenBy { it.id })
        }

    fun observeFavorites(): Flow<List<Word>> =
        storage.words.map { words -> words.filter { it.isFavorite }.sortedByTurkish() }

    /** Carga el vocabulario inicial solo si todavía no hay palabras guardadas. */
    suspend fun seedIfEmpty() {
        storage.updateWords { current ->
            if (current.isEmpty()) SeedData.words.mapIndexed { i, w -> w.copy(id = i + 1L) } else current
        }
    }

    suspend fun toggleFavorite(word: Word) {
        storage.updateWords { words ->
            words.map { if (it.id == word.id) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    /** Devuelve false si la palabra ya existe en esa categoría. */
    suspend fun addCustomWord(turkish: String, spanish: String, category: Category, emoji: String): Boolean {
        val cleanTurkish = turkish.trim()
        var added = false
        storage.updateWords { words ->
            val duplicated = words.any {
                it.category == category.name && it.turkish.equals(cleanTurkish, ignoreCase = true)
            }
            if (duplicated) words
            else {
                added = true
                words + Word(
                    id = (words.maxOfOrNull { it.id } ?: 0L) + 1L,
                    turkish = cleanTurkish,
                    spanish = spanish.trim(),
                    category = category.name,
                    emoji = emoji.trim().ifEmpty { category.emoji },
                    isCustom = true
                )
            }
        }
        return added
    }

    suspend fun delete(word: Word) {
        storage.updateWords { words -> words.filter { it.id != word.id } }
    }
}
