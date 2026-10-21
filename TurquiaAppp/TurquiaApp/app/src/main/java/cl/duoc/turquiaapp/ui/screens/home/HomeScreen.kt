package cl.duoc.turquiaapp.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.turquiaapp.domain.model.Category
import cl.duoc.turquiaapp.ui.components.CategoryPalette
import cl.duoc.turquiaapp.ui.components.ImagenInteligente
import cl.duoc.turquiaapp.viewmodel.AppViewModelProvider
import cl.duoc.turquiaapp.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onCategoryClick: (Category) -> Unit,
    onTranslatorClick: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val counts by viewModel.counts.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ImagenInteligente(photoPath = user?.photoPath, size = 64.dp)
                    Column {
                        Text(
                            "¡Merhaba, ${user?.childName ?: ""}!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            "¿Qué palabras aprendemos hoy?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Button(
                onClick = onTranslatorClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Translate, contentDescription = null)
                Text("  Abrir traductor", style = MaterialTheme.typography.titleMedium)
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                "Categorías",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        itemsIndexed(Category.entries.toList()) { index, category ->
            CategoryCard(
                category = category,
                wordCount = counts[category] ?: 0,
                color = CategoryPalette[index % CategoryPalette.size],
                index = index,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

/** Tarjeta de categoría: entra con retraso escalonado y se encoge al presionarla. */
@Composable
private fun CategoryCard(
    category: Category,
    wordCount: Int,
    color: Color,
    index: Int,
    onClick: () -> Unit
) {
    val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressScale by animateFloatAsState(if (pressed) 0.94f else 1f, tween(120), label = "pressScale")

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(tween(350, delayMillis = index * 90)) +
            scaleIn(tween(350, delayMillis = index * 90), initialScale = 0.8f)
    ) {
        Card(
            onClick = onClick,
            interactionSource = interaction,
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = color, contentColor = Color(0xFF1B1B1F)),
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .scale(pressScale)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(category.emoji, fontSize = 44.sp)
                Column {
                    Text(category.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("$wordCount palabras", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
