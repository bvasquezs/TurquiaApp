package com.example.appturconinos.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appturconinos.viewmodel.UsuarioViewModel

@Composable
fun RegistroScreen(
    navController: NavController,
    viewModel: UsuarioViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val retoActual = estado.listaRetos[estado.indiceActual]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🌟 ¡Aventura en Español! 🌟",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Contador de progreso
        Text(
            text = "Palabra ${estado.indiceActual + 1} de ${estado.listaRetos.size}",
            style = MaterialTheme.typography.labelLarge
        )

        OutlinedTextField(
            value = estado.estudiante,
            onValueChange = viewModel::onEstudianteChange,
            label = { Text("Nombre del aventurero 🧒") },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Traduce esta palabra de Turco a Español:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = retoActual.turco,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = retoActual.pista, style = MaterialTheme.typography.bodySmall)
            }
        }

        OutlinedTextField(
            value = estado.respuestaEspanol,
            onValueChange = viewModel::onRespuestaChange,
            label = { Text("¿Cómo se escribe en español? ✍️") },
            isError = estado.errores.respuestaEspanol != null,
            supportingText = {
                estado.errores.respuestaEspanol?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (viewModel.validarYComprobar()) {
                    navController.navigate("resumen")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "¡Comprobar Respuesta! 🚀", style = MaterialTheme.typography.titleMedium)
        }
    }
}