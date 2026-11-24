package cl.duoc.turquiaapp.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.turquiaapp.ui.components.ErrorBanner
import cl.duoc.turquiaapp.ui.components.LoadingButton
import cl.duoc.turquiaapp.ui.components.ValidatedTextField
import cl.duoc.turquiaapp.ui.components.shake
import cl.duoc.turquiaapp.viewmodel.AppViewModelProvider
import cl.duoc.turquiaapp.viewmodel.LoginViewModel
import cl.duoc.turquiaapp.viewmodel.RegisterViewModel

/** Estructura común de login y registro: fondo degradado, logo animado y tarjeta con el formulario. */
@Composable
private fun AuthLayout(
    title: String,
    subtitle: String,
    shakeTrigger: Int,
    content: @Composable () -> Unit
) {
    val appear = remember { MutableTransitionState(false).apply { targetState = true } }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.background),
                    endY = 1400f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            AnimatedVisibility(visibleState = appear, enter = scaleIn() + fadeIn()) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) { Text("🗣️", fontSize = 44.sp) }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            AnimatedVisibility(
                visibleState = appear,
                enter = fadeIn() + slideInVertically { it / 4 }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shake(shakeTrigger),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) { content() }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoRegister: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.success) { if (state.success) onLoginSuccess() }

    AuthLayout(
        title = "Merhaba!",
        subtitle = "Aprende turco jugando",
        shakeTrigger = state.submitAttempts
    ) {
        Text("Iniciar sesión", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        ErrorBanner(state.generalError)
        ValidatedTextField(
            field = state.email,
            onValueChange = viewModel::onEmailChange,
            label = "Correo electrónico",
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email
        )
        ValidatedTextField(
            field = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Contraseña",
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
            imeAction = ImeAction.Done,
            onImeAction = viewModel::submit
        )
        Spacer(Modifier.height(4.dp))
        LoadingButton("Entrar", onClick = viewModel::submit, isLoading = state.isLoading)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿No tienes cuenta?", style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onGoRegister) { Text("Regístrate") }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onGoLogin: () -> Unit,
    viewModel: RegisterViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.success) { if (state.success) onRegisterSuccess() }

    AuthLayout(
        title = "Crear cuenta",
        subtitle = "Un adulto registra al niño o niña que aprenderá turco",
        shakeTrigger = state.submitAttempts
    ) {
        Text("Datos del tutor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        ErrorBanner(state.generalError)
        ValidatedTextField(
            field = state.name,
            onValueChange = viewModel::onNameChange,
            label = "Tu nombre",
            leadingIcon = Icons.Filled.Person,
            capitalization = KeyboardCapitalization.Words
        )
        ValidatedTextField(
            field = state.email,
            onValueChange = viewModel::onEmailChange,
            label = "Correo electrónico",
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email
        )
        ValidatedTextField(
            field = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Contraseña",
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
            helper = "Mínimo 8 caracteres, un número y una mayúscula"
        )
        ValidatedTextField(
            field = state.confirm,
            onValueChange = viewModel::onConfirmChange,
            label = "Repite la contraseña",
            leadingIcon = Icons.Filled.Lock,
            isPassword = true
        )
        Spacer(Modifier.height(4.dp))
        Text("Datos del niño o niña", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        ValidatedTextField(
            field = state.childName,
            onValueChange = viewModel::onChildNameChange,
            label = "Nombre del niño/a",
            leadingIcon = Icons.Filled.Face,
            capitalization = KeyboardCapitalization.Words
        )
        ValidatedTextField(
            field = state.childAge,
            onValueChange = viewModel::onChildAgeChange,
            label = "Edad (3 a 12 años)",
            leadingIcon = Icons.Filled.Cake,
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
            onImeAction = viewModel::submit
        )
        Spacer(Modifier.height(4.dp))
        LoadingButton("Crear cuenta", onClick = viewModel::submit, isLoading = state.isLoading)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿Ya tienes cuenta?", style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onGoLogin) { Text("Inicia sesión") }
        }
    }
}
