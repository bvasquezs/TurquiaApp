package cl.duoc.turquiaapp.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.turquiaapp.domain.model.Category
import cl.duoc.turquiaapp.ui.components.CategoryPalette
import cl.duoc.turquiaapp.ui.components.ImagenInteligente
import cl.duoc.turquiaapp.ui.theme.tarjetaInfantil3D
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
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            val primaryColor = MaterialTheme.colorScheme.primary
            val borderSombra = lerp(primaryColor, Color.Black, 0.25f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .tarjetaInfantil3D(
                        colorFondo = primaryColor,
                        colorBordeSombra = borderSombra,
                        radio = 26.dp,
                        grosorSombra = 6.dp
                    )
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ImagenInteligente(photoPath = user?.photoPath, size = 68.dp)
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
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.92f)
                        )
                    }
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            val interaction = remember { MutableInteractionSource() }
            val pressed by interaction.collectIsPressedAsState()
            val scale by animateFloatAsState(if (pressed) 0.96f else 1f, tween(100), label = "btnScale")

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale)
                    .tarjetaInfantil3D(
                        colorFondo = Color(0xFFFFCA3A),
                        colorBordeSombra = Color(0xFFE09F3E),
                        radio = 22.dp,
                        grosorSombra = 5.dp
                    )
                    .clickable(interactionSource = interaction, indication = null) {
                        onTranslatorClick()
                    }
                    .padding(vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Filled.Translate,
                        contentDescription = null,
                        tint = Color(0xFF3D2C00)
                    )
                    Text(
                        "Abrir traductor mágico",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF3D2C00)
                    )
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                "Categorías",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 4.dp)
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

    val bordeSombra = lerp(color, Color.Black, 0.22f)

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(tween(350, delayMillis = index * 90)) +
                scaleIn(tween(350, delayMillis = index * 90), initialScale = 0.8f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(152.dp)
                .scale(pressScale)
                .tarjetaInfantil3D(
                    colorFondo = color,
                    colorBordeSombra = bordeSombra,
                    radio = 24.dp,
                    grosorSombra = 6.dp
                )
                .clickable(interactionSource = interaction, indication = null) {
                    onClick()
                }
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(category.emoji, fontSize = 48.sp)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        category.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2B2D42)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.65f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            "$wordCount palabras",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2B2D42)
                        )
                    }
                }
            }
        }
    }
}