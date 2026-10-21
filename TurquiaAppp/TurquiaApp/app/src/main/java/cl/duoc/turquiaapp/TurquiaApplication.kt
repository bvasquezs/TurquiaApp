package cl.duoc.turquiaapp

import android.app.Application
import cl.duoc.turquiaapp.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TurquiaApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        // Carga el vocabulario inicial la primera vez que se abre la app.
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            container.wordRepository.seedIfEmpty()
        }
    }
}
