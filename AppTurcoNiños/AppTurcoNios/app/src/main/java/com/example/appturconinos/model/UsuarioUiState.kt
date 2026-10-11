package com.example.appturconinos.model

data class PalabraReto(
    val turco: String,
    val espanolCorrecto: String,
    val pista: String
)

// Guarda el resultado de cada intento del niño
data class ResultadoPalabra(
    val turco: String,
    val espanolCorrecto: String,
    val respuestaNino: String,
    val esCorrecta: Boolean
)

data class UsuarioErrores(
    val respuestaEspanol: String? = null
)

data class UsuarioUiState(
    val estudiante: String = "",
    val indiceActual: Int = 0,
    val respuestaEspanol: String = "",
    val esCorrecto: Boolean? = null,
    val errores: UsuarioErrores = UsuarioErrores(),
    val historialResultados: List<ResultadoPalabra> = emptyList(),
    val juegoTerminado: Boolean = false,

    val listaRetos: List<PalabraReto> = listOf(
        PalabraReto("Merhaba", "Hola", "¡Para saludar! 👋"),
        PalabraReto("Su", "Agua", "¡Para cuando tienes sed! 💧"),
        PalabraReto("Kedi", "Gato", "¡Un tierno animal que dice miau! 🐱"),
        PalabraReto("Köpek", "Perro", "¡El mejor amigo del hombre! 🐶"),
        PalabraReto("Elma", "Manzana", "¡Una fruta roja y rica! 🍎")
    )
)