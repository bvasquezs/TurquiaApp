package cl.duoc.turquiaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.turquiaapp.domain.model.Word
import cl.duoc.turquiaapp.data.repository.WordRepository
import cl.duoc.turquiaapp.domain.text.normalizeForSearch
import cl.duoc.turquiaapp.domain.validation.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class Direction(val from: String, val to: String) {
    TR_TO_ES("Turco", "Español"),
    ES_TO_TR("Español", "Turco");

    fun reversed(): Direction = if (this == TR_TO_ES) ES_TO_TR else TR_TO_ES
}

data class TranslatorUiState(
    val input: FieldState = FieldState(),
    val direction: Direction = Direction.TR_TO_ES,
    val results: List<Word> = emptyList()
) {
    val hasQuery: Boolean get() = input.value.isNotBlank() && input.error == null
}

class TranslatorViewModel(wordRepository: WordRepository) : ViewModel() {
    private val input = MutableStateFlow(FieldState())
    private val direction = MutableStateFlow(Direction.TR_TO_ES)

    val state: StateFlow<TranslatorUiState> = combine(
        input, direction, wordRepository.observeAll()
    ) { field, dir, words ->
        TranslatorUiState(field, dir, if (field.error == null) search(words, field.value, dir) else emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TranslatorUiState())

    fun onInputChange(v: String) {
        input.value = FieldState(v, Validators.searchText(v))
    }

    fun swapDirection() {
        direction.value = direction.value.reversed()
    }

    fun clear() {
        input.value = FieldState()
    }

    /** Coincidencias exactas primero, luego palabras que empiezan o contienen el texto. */
    private fun search(words: List<Word>, query: String, dir: Direction): List<Word> {
        val q = query.normalizeForSearch()
        if (q.isEmpty()) return emptyList()
        fun source(w: Word) = (if (dir == Direction.TR_TO_ES) w.turkish else w.spanish).normalizeForSearch()
        return words.filter { source(it).contains(q) }
            .sortedBy { w ->
                val s = source(w)
                when {
                    s == q -> 0
                    s.startsWith(q) -> 1
                    else -> 2
                }
            }
            .take(15)
    }
}
