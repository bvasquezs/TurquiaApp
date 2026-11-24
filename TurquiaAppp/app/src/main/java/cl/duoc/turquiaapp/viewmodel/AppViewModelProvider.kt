package cl.duoc.turquiaapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.turquiaapp.TurquiaApplication

private fun CreationExtras.container() =
    (this[APPLICATION_KEY] as TurquiaApplication).container

/** Fábrica única de ViewModels: inyecta los repositorios desde el AppContainer. */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { LoginViewModel(container().userRepository) }
        initializer { RegisterViewModel(container().userRepository) }
        initializer { HomeViewModel(container().userRepository, container().wordRepository) }
        initializer { WordsViewModel(createSavedStateHandle(), container().wordRepository) }
        initializer { AddWordViewModel(container().wordRepository) }
        initializer { TranslatorViewModel(container().wordRepository) }
        initializer { ProfileViewModel(container().userRepository) }
    }
}
