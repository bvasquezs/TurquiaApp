package cl.duoc.turquiaapp.ui.screens.translator

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.turquiaapp.domain.model.Word
import cl.duoc.turquiaapp.ui.components.EmptyState
import cl.duoc.turquiaapp.ui.components.ValidatedTextField
import cl.duoc.turquiaapp.viewmodel.AppViewModelProvider
import cl.duoc.turquiaapp.viewmodel.Direction
import cl.duoc.turquiaapp.viewmodel.TranslatorViewModel

@Composable
fun TranslatorScreen(
    onAddWord: () -> Unit,
    viewModel: TranslatorViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Traductor", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)

            DirectionCard(direction = state.direction, onSwap = viewModel::swapDirection)

            ValidatedTextField(
                field = state.input,
                onValueChange = viewModel::onInputChange,
                label = "Escribe en ${state.direction.from}",
                leadingIcon = Icons.Filled.Translate,
                imeAction = ImeAction.Search,
                extraTrailing = {
                    if (state.input.value.isNotEmpty()) {
                        IconButton(onClick = viewModel::clear) {
                            Icon(Icons.Filled.Clear, contentDescription = "Borrar texto")
                        }
                    }
                }
            )

            Box(Modifier.fillMaxWidth().weight(1f)) {
                when {
                    !state.hasQuery -> EmptyState(
                        emoji = "💬",
                        title = "¿Qué palabra buscas?",
                        subtitle = "Escríbela para ver su traducción",
                        modifier = Modifier.align(Alignment.Center)
                    )
                    state.results.isEmpty() -> Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EmptyState(
                            emoji = "🤔",
                            title = "No encontré esa palabra",
                            subtitle = "¿Quieres agregarla a tu diccionario?"
                        )
                        Button(onClick = onAddWord) { Text("Agregar palabra") }
                    }
                    else -> LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.results, key = { it.id }) { word ->
                            TranslationCard(word = word, direction = state.direction)
                        }
                    }
                }
            }
        }
    }
}

/** Muestra "Turco ⇄ Español"; al cambiar la dirección el ícono gira. */
@Composable
private fun DirectionCard(direction: Direction, onSwap: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (direction == Direction.TR_TO_ES) 0f else 180f,
        animationSpec = tween(350),
        label = "swapRotation"
    )
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(direction.from, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = onSwap) {
                Icon(
                    Icons.Filled.SwapHoriz,
                    contentDescription = "Cambiar dirección de traducción",
                    modifier = Modifier.rotate(rotation)
                )
            }
            Text(direction.to, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TranslationCard(word: Word, direction: Direction) {
    val source = if (direction == Direction.TR_TO_ES) word.turkish else word.spanish
    val target = if (direction == Direction.TR_TO_ES) word.spanish else word.turkish
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(word.emoji, fontSize = 36.sp)
            Column(Modifier.weight(1f)) {
                Text(source, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    target,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
