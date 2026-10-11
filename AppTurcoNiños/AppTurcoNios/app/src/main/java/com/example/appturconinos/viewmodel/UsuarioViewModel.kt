package com.example.appturconinos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appturconinos.model.ResultadoPalabra
import com.example.appturconinos.model.UsuarioErrores
import com.example.appturconinos.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class UsuarioViewModel : ViewModel() {
    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado

    fun onEstudianteChange(valor: String) {
        _estado.update { it.copy(estudiante = valor) }
    }

    fun onRespuestaChange(valor: String) {
        _estado.update {
            it.copy(
                respuestaEspanol = valor,
                errores = it.errores.copy(respuestaEspanol = null)
            )
        }
    }

    fun validarYComprobar(): Boolean {
        val estadoActual = _estado.value

        if (estadoActual.respuestaEspanol.isBlank()) {
            _estado.update {
                it.copy(errores = UsuarioErrores(respuestaEspanol = "¡Escribe una respuesta, amiguito! 🎈"))
            }
            return false
        }

        val retoActual = estadoActual.listaRetos[estadoActual.indiceActual]
        val esAcertado = estadoActual.respuestaEspanol.trim().equals(retoActual.espanolCorrecto, ignoreCase = true)

        val nuevoResultado = ResultadoPalabra(
            turco = retoActual.turco,
            espanolCorrecto = retoActual.espanolCorrecto,
            respuestaNino = estadoActual.respuestaEspanol.trim(),
            esCorrecta = esAcertado
        )

        _estado.update {
            it.copy(
                esCorrecto = esAcertado,
                historialResultados = it.historialResultados + nuevoResultado,
                errores = UsuarioErrores()
            )
        }

        return true
    }

    fun siguienteReto(): Boolean {
        val estadoActual = _estado.value
        val esUltima = estadoActual.indiceActual >= estadoActual.listaRetos.size - 1

        if (esUltima) {
            _estado.update { it.copy(juegoTerminado = true) }
            return true // Indica que ya terminaron todas las palabras
        } else {
            _estado.update {
                it.copy(
                    indiceActual = it.indiceActual + 1,
                    respuestaEspanol = "",
                    esCorrecto = null
                )
            }
            return false
        }
    }

    fun reiniciarJuego() {
        _estado.update {
            UsuarioUiState(
                estudiante = it.estudiante, // Mantenemos el nombre del estudiante
                listaRetos = it.listaRetos
            )
        }
    }
}