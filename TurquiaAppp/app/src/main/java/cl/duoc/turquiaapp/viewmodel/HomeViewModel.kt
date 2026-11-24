package cl.duoc.turquiaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.turquiaapp.domain.model.User
import cl.duoc.turquiaapp.data.repository.UserRepository
import cl.duoc.turquiaapp.data.repository.WordRepository
import cl.duoc.turquiaapp.domain.model.Category
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(userRepository: UserRepository, wordRepository: WordRepository) : ViewModel() {

    val user: StateFlow<User?> = userRepository.observeCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Cantidad de palabras por categoría para mostrar en cada tarjeta. */
    val counts: StateFlow<Map<Category, Int>> = wordRepository.observeAll()
        .map { words -> words.mapNotNull { Category.fromKey(it.category) }.groupingBy { it }.eachCount() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())
}
