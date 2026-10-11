package com.example.appturconinos.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appturconinos.viewmodel.UsuarioViewModel

@Composable
fun ResumenScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.estado.collectAsState()
    val retoActual = estado.listaRetos[estado.indiceActual]

    if (estado.juegoTerminado) {
        // --- VISTA FINAL: RESUMEN DE TODAS LAS PALABRAS ---
        val aciertosTotales = estado.historialResultados.count { it.esCorrecta }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏆 ¡Juego Completado! 🏆",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "¡Gran trabajo, ${estado.estudiante}!",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Acertaste $aciertosTotales de ${estado.listaRetos.size} palabras",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista con el detalle de las buenas y malas
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estado.historialResultados) { res ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (res.esCorrecta)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${res.turco} -> ${res.espanolCorrecto}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Tu respuesta: ${res.respuestaNino}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                text = if (res.esCorrecta) "✅" else "❌",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para volver a jugar
            Button(
                onClick = {
                    viewModel.reiniciarJuego()
                    navController.popBackStack("registro", inclusive = false)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "🔄 Intentarlo nuevamente", style = MaterialTheme.typography.titleMedium)
            }
        }
    } else {
        // --- VISTA INTERMEDIA: RESULTADO DE CADA PALABRA ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (estado.esCorrecto == true) {
                Text(
                    text = "🎉 ¡EXCELENTE! 🎉",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "¡Muy bien ${estado.estudiante}! Lo hiciste genial.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "'${retoActual.turco}' es '${retoActual.espanolCorrecto}' en español.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = "💪 ¡CASI LO LOGRAS! 💪",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No te preocupes ${estado.estudiante}, ¡se aprende practicando!",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "La respuesta correcta para '${retoActual.turco}' era '${retoActual.espanolCorrecto}'.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val esFin = viewModel.siguienteReto()
                    if (!esFin) {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                val esUltima = estado.indiceActual == estado.listaRetos.size - 1
                Text(
                    text = if (esUltima) "🏁 Ver resultados finales" else "¡Siguiente Palabra! ➡️",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}