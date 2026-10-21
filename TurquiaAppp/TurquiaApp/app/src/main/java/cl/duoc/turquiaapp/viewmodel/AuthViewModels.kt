package cl.duoc.turquiaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.turquiaapp.data.repository.AuthResult
import cl.duoc.turquiaapp.data.repository.UserRepository
import cl.duoc.turquiaapp.domain.validation.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ---------------------------------------------------------------- Login

data class LoginUiState(
    val email: FieldState = FieldState(),
    val password: FieldState = FieldState(),
    val isLoading: Boolean = false,
    val submitAttempts: Int = 0,
    val generalError: String? = null,
    val success: Boolean = false
)

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChange(v: String) =
        _state.update { it.copy(email = FieldState(v, Validators.email(v)), generalError = null) }

    fun onPasswordChange(v: String) =
        _state.update { it.copy(password = FieldState(v, Validators.loginPassword(v)), generalError = null) }

    fun submit() {
        val s = _state.value
        if (s.isLoading) return
        val validated = s.copy(
            email = s.email.copy(error = Validators.email(s.email.value)),
            password = s.password.copy(error = Validators.loginPassword(s.password.value))
        )
        if (validated.email.error != null || validated.password.error != null) {
            _state.value = validated.copy(submitAttempts = s.submitAttempts + 1)
            return
        }
        _state.value = validated.copy(isLoading = true, generalError = null)
        viewModelScope.launch {
            when (val result = repository.login(s.email.value, s.password.value)) {
                is AuthResult.Success -> _state.update { it.copy(isLoading = false, success = true) }
                is AuthResult.Error -> _state.update {
                    it.copy(isLoading = false, generalError = result.message, submitAttempts = it.submitAttempts + 1)
                }
            }
        }
    }
}

// ------------------------------------------------------------- Registro

data class RegisterUiState(
    val name: FieldState = FieldState(),
    val email: FieldState = FieldState(),
    val password: FieldState = FieldState(),
    val confirm: FieldState = FieldState(),
    val childName: FieldState = FieldState(),
    val childAge: FieldState = FieldState(),
    val isLoading: Boolean = false,
    val submitAttempts: Int = 0,
    val generalError: String? = null,
    val success: Boolean = false
) {
    val hasErrors: Boolean
        get() = listOf(name, email, password, confirm, childName, childAge).any { it.error != null }
}

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onNameChange(v: String) =
        _state.update { it.copy(name = FieldState(v, Validators.personName(v)), generalError = null) }

    fun onEmailChange(v: String) =
        _state.update { it.copy(email = FieldState(v, Validators.email(v)), generalError = null) }

    fun onPasswordChange(v: String) = _state.update {
        // Si ya se escribió la confirmación, se vuelve a validar contra la nueva contraseña.
        val confirm = if (it.confirm.value.isEmpty()) it.confirm
        else FieldState(it.confirm.value, Validators.confirmPassword(v, it.confirm.value))
        it.copy(password = FieldState(v, Validators.password(v)), confirm = confirm, generalError = null)
    }

    fun onConfirmChange(v: String) = _state.update {
        it.copy(confirm = FieldState(v, Validators.confirmPassword(it.password.value, v)), generalError = null)
    }

    fun onChildNameChange(v: String) =
        _state.update { it.copy(childName = FieldState(v, Validators.personName(v))) }

    fun onChildAgeChange(v: String) {
        val digits = v.filter { it.isDigit() }.take(2)
        _state.update { it.copy(childAge = FieldState(digits, Validators.childAge(digits))) }
    }

    fun submit() {
        val s = _state.value
        if (s.isLoading) return
        val validated = s.copy(
            name = s.name.copy(error = Validators.personName(s.name.value)),
            email = s.email.copy(error = Validators.email(s.email.value)),
            password = s.password.copy(error = Validators.password(s.password.value)),
            confirm = s.confirm.copy(error = Validators.confirmPassword(s.password.value, s.confirm.value)),
            childName = s.childName.copy(error = Validators.personName(s.childName.value)),
            childAge = s.childAge.copy(error = Validators.childAge(s.childAge.value))
        )
        if (validated.hasErrors) {
            _state.value = validated.copy(submitAttempts = s.submitAttempts + 1)
            return
        }
        _state.value = validated.copy(isLoading = true, generalError = null)
        viewModelScope.launch {
            val result = repository.register(
                name = s.name.value,
                email = s.email.value,
                password = s.password.value,
                childName = s.childName.value,
                childAge = s.childAge.value.toInt()
            )
            when (result) {
                is AuthResult.Success -> _state.update { it.copy(isLoading = false, success = true) }
                is AuthResult.Error -> _state.update {
                    it.copy(isLoading = false, generalError = result.message, submitAttempts = it.submitAttempts + 1)
                }
            }
        }
    }
}
