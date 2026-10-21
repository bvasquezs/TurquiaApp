package cl.duoc.turquiaapp.ui.screens.words

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.turquiaapp.domain.model.Word
import cl.duoc.turquiaapp.domain.model.Category
import cl.duoc.turquiaapp.ui.components.EmptyState
import cl.duoc.turquiaapp.ui.components.ErrorBanner
import cl.duoc.turquiaapp.ui.components.LoadingButton
import cl.duoc.turquiaapp.ui.components.ValidatedTextField
import cl.duoc.turquiaapp.ui.components.WordCard
import cl.duoc.turquiaapp.ui.components.shake
import cl.duoc.turquiaapp.viewmodel.AddWordViewModel
import cl.duoc.turquiaapp.viewmodel.AppViewModelProvider
import cl.duoc.turquiaapp.viewmodel.WordsViewModel
import kotlinx.coroutines.launch

/** Lista de palabras de una categoría. Sin categoría (viewModel.category == null) muestra los favoritos. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WordsScreen(
    onBack: (() -> Unit)?,
    onAddWord: () -> Unit,
    viewModel: WordsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val words by viewModel.words.collectAsStateWithLifecycle()
    val category = viewModel.category
    var wordToDelete by remember { mutableStateOf<Word?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (category != null) "${category.emoji} ${category.label}" else "❤️ Mis favoritas",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddWord,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Agregar palabra") }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val list = words
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                list == null -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                list.isEmpty() -> EmptyState(
                    emoji = if (category == null) "💔" else "📭",
                    title = if (category == null) "Aún no tienes favoritas" else "No hay palabras",
                    subtitle = if (category == null) "Toca el corazón ❤️ en una palabra para guardarla aquí"
                    else "Agrega la primera con el botón de abajo",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(list, key = { it.id }) { word ->
                        WordCard(
                            word = word,
                            modifier = Modifier.animateItem(),
                            onToggleFavorite = { viewModel.toggleFavorite(word) },
                            onDelete = if (word.isCustom) ({ wordToDelete = word }) else null
                        )
                    }
                }
            }
        }
    }

    wordToDelete?.let { word ->
        AlertDialog(
            onDismissRequest = { wordToDelete = null },
            title = { Text("Eliminar palabra") },
            text = { Text("¿Quieres eliminar \"${word.turkish}\"?") },
            confirmButton = {
                TextButton(onClick = { viewModel.delete(word); wordToDelete = null }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { wordToDelete = null }) { Text("Cancelar") } }
        )
    }
}

/** Formulario para agregar una palabra propia al diccionario. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddWordScreen(
    onBack: () -> Unit,
    viewModel: AddWordViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.saved) {
        if (state.saved) {
            Toast.makeText(context, "¡Palabra agregada! 🎉", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva palabra", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shake(state.submitAttempts),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ErrorBanner(state.generalError)
                    ValidatedTextField(
                        field = state.turkish,
                        onValueChange = viewModel::onTurkishChange,
                        label = "Palabra en turco",
                        leadingIcon = Icons.Filled.Translate,
                        capitalization = KeyboardCapitalization.Sentences
                    )
                    ValidatedTextField(
                        field = state.spanish,
                        onValueChange = viewModel::onSpanishChange,
                        label = "Significado en español",
                        leadingIcon = Icons.Filled.Translate,
                        capitalization = KeyboardCapitalization.Sentences
                    )
                    ValidatedTextField(
                        field = state.emoji,
                        onValueChange = viewModel::onEmojiChange,
                        label = "Emoji (opcional)",
                        leadingIcon = Icons.Filled.EmojiEmotions,
                        helper = "Si lo dejas vacío usaremos el de la categoría",
                        imeAction = ImeAction.Done
                    )
                    Text("Categoría", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Category.entries.forEach { category ->
                            FilterChip(
                                selected = state.category == category,
                                onClick = { viewModel.onCategoryChange(category) },
                                label = { Text("${category.emoji} ${category.label}") },
                                leadingIcon = if (state.category == category) {
                                    { Icon(Icons.Filled.Check, contentDescription = null) }
                                } else null
                            )
                        }
                    }
                    Text(
                        text = state.categoryError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.animateContentSize()
                    )
                    Spacer(Modifier.height(4.dp))
                    LoadingButton("Guardar palabra", onClick = viewModel::submit, isLoading = state.isSaving)
                }
            }
        }
    }
}
