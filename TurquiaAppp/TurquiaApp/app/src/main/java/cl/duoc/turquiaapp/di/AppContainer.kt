package cl.duoc.turquiaapp.di

import android.content.Context
import cl.duoc.turquiaapp.data.local.LocalStorage
import cl.duoc.turquiaapp.data.local.SessionManager
import cl.duoc.turquiaapp.data.repository.UserRepository
import cl.duoc.turquiaapp.data.repository.WordRepository

/** Contenedor de dependencias manual: crea el almacenamiento local y los repositorios una sola vez. */
class AppContainer(context: Context) {
    private val storage = LocalStorage(context)
    val session = SessionManager(context)
    val userRepository = UserRepository(storage, session)
    val wordRepository = WordRepository(storage)
}
