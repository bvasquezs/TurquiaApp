package cl.duoc.turquiaapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.turquiaapp.domain.model.Word
import cl.duoc.turquiaapp.data.repository.WordRepository
import cl.duoc.turquiaapp.domain.model.Category
import cl.duoc.turquiaapp.domain.validation.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Lista de palabras de una categoría; sin argumento "category" muestra los favoritos. */
class WordsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: WordRepository
) : ViewModel() {

    val category: Category? = Category.fromKey(savedStateHandle.get<String>("category"))

    /** null = todavía cargando. */
    val words: StateFlow<List<Word>?> =
        (if (category != null) repository.observeByCategory(category) else repository.observeFavorites())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun toggleFavorite(word: Word) {
        viewModelScope.launch { repository.toggleFavorite(word) }
    }

    fun delete(word: Word) {
        viewModelScope.launch { repository.delete(word) }
    }
}

data class AddWordUiState(
    val turkish: FieldState = FieldState(),
    val spanish: FieldState = FieldState(),
    val emoji: FieldState = FieldState(),
    val category: Category? = null,
    val categoryError: String? = null,
    val submitAttempts: Int = 0,
    val isSaving: Boolean = false,
    val generalError: String? = null,
    val saved: Boolean = false
)

class AddWordViewModel(private val repository: WordRepository) : ViewModel() {
    private val _state = MutableStateFlow(AddWordUiState())
    val state: StateFlow<AddWordUiState> = _state.asStateFlow()

    fun onTurkishChange(v: String) =
        _state.update { it.copy(turkish = FieldState(v, Validators.word(v, "la palabra en turco")), generalError = null) }

    fun onSpanishChange(v: String) =
        _state.update { it.copy(spanish = FieldState(v, Validators.word(v, "la traducción al español"))) }

    fun onEmojiChange(v: String) =
        _state.update { it.copy(emoji = FieldState(v.take(4))) }

    fun onCategoryChange(c: Category) =
        _state.update { it.copy(category = c, categoryError = null, generalError = null) }

    fun submit() {
        val s = _state.value
        if (s.isSaving) return
        val validated = s.copy(
            turkish = s.turkish.copy(error = Validators.word(s.turkish.value, "la palabra en turco")),
            spanish = s.spanish.copy(error = Validators.word(s.spanish.value, "la traducción al español")),
            categoryError = Validators.category(s.category)
        )
        val category = validated.category
        if (validated.turkish.error != null || validated.spanish.error != null || category == null) {
            _state.value = validated.copy(submitAttempts = s.submitAttempts + 1)
            return
        }
        _state.value = validated.copy(isSaving = true, generalError = null)
        viewModelScope.launch {
            val added = repository.addCustomWord(s.turkish.value, s.spanish.value, category, s.emoji.value)
            _state.update {
                if (added) it.copy(isSaving = false, saved = true)
                else it.copy(
                    isSaving = false,
                    generalError = "Esa palabra ya existe en ${category.label}",
                    submitAttempts = it.submitAttempts + 1
                )
            }
        }
    }
}
