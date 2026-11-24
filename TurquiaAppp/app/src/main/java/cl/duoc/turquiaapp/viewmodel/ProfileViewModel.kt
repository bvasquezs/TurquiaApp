package cl.duoc.turquiaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.turquiaapp.domain.model.User
import cl.duoc.turquiaapp.data.repository.UserRepository
import cl.duoc.turquiaapp.domain.validation.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userId: Long = 0,
    val tutorName: String = "",
    val email: String = "",
    val childName: FieldState = FieldState(),
    val childAge: FieldState = FieldState(),
    val photoPath: String? = null,
    val loaded: Boolean = false,
    val submitAttempts: Int = 0,
    val savedMessage: String? = null
)

class ProfileViewModel(private val repository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeCurrentUser().first { it != null }?.let(::load)
        }
    }

    private fun load(user: User) = _state.update {
        it.copy(
            userId = user.id,
            tutorName = user.name,
            email = user.email,
            childName = FieldState(user.childName),
            childAge = FieldState(user.childAge.toString()),
            photoPath = user.photoPath,
            loaded = true
        )
    }

    fun onChildNameChange(v: String) =
        _state.update { it.copy(childName = FieldState(v, Validators.personName(v)), savedMessage = null) }

    fun onChildAgeChange(v: String) {
        val digits = v.filter { it.isDigit() }.take(2)
        _state.update { it.copy(childAge = FieldState(digits, Validators.childAge(digits)), savedMessage = null) }
    }

    /** La foto se guarda de inmediato para no perderla si la app se cierra. */
    fun onPhotoTaken(path: String) {
        val s = _state.value
        _state.update { it.copy(photoPath = path) }
        viewModelScope.launch {
            val age = s.childAge.value.toIntOrNull() ?: return@launch
            if (s.childName.error == null && s.childAge.error == null) {
                repository.updateProfile(s.userId, s.childName.value, age, path)
            }
        }
    }

    fun save() {
        val s = _state.value
        val nameError = Validators.personName(s.childName.value)
        val ageError = Validators.childAge(s.childAge.value)
        if (nameError != null || ageError != null) {
            _state.update {
                it.copy(
                    childName = it.childName.copy(error = nameError),
                    childAge = it.childAge.copy(error = ageError),
                    submitAttempts = it.submitAttempts + 1
                )
            }
            return
        }
        viewModelScope.launch {
            repository.updateProfile(s.userId, s.childName.value, s.childAge.value.toInt(), s.photoPath)
            _state.update { it.copy(savedMessage = "¡Perfil guardado!") }
        }
    }

    fun logout() = repository.logout()
}
