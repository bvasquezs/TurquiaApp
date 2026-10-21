package cl.duoc.turquiaapp.ui.screens.profile

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.turquiaapp.device.CameraSupport
import cl.duoc.turquiaapp.ui.components.ImagenInteligente
import cl.duoc.turquiaapp.ui.components.LoadingButton
import cl.duoc.turquiaapp.ui.components.ValidatedTextField
import cl.duoc.turquiaapp.ui.components.openAppSettings
import cl.duoc.turquiaapp.ui.components.shake
import cl.duoc.turquiaapp.viewmodel.AppViewModelProvider
import cl.duoc.turquiaapp.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.savedMessage) {
        state.savedMessage?.let { snackbar.showSnackbar(it) }
    }

    // --- Recurso nativo: cámara (permiso en tiempo de ejecución + FileProvider) ---
    // La ruta pendiente se guarda con rememberSaveable por si Android recrea la actividad
    // mientras la app de cámara está abierta.
    var pendingPath by rememberSaveable { mutableStateOf<String?>(null) }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val path = pendingPath
        pendingPath = null
        if (success && path != null) {
            state.photoPath?.let { File(it).delete() } // reemplaza la foto anterior
            viewModel.onPhotoTaken(path)
        } else {
            path?.let { File(it).delete() }
        }
    }

    fun launchCamera() {
        val (file, uri) = CameraSupport.newPhotoTarget(context, state.userId)
        pendingPath = file.absolutePath
        takePicture.launch(uri)
    }

    val cameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) launchCamera() else scope.launch {
            val result = snackbar.showSnackbar(
                message = "Necesito la cámara para tomar la foto 📷",
                actionLabel = "Ajustes"
            )
            if (result == SnackbarResult.ActionPerformed) openAppSettings(context)
        }
    }

    fun onCameraClick() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) launchCamera() else cameraPermission.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Mi perfil", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)

            Box(contentAlignment = Alignment.BottomEnd) {
                ImagenInteligente(photoPath = state.photoPath, size = 132.dp)
                FilledIconButton(onClick = ::onCameraClick, modifier = Modifier.size(44.dp)) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = "Tomar foto de perfil")
                }
            }
            Text(
                "Tutor: ${state.tutorName}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                state.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shake(state.submitAttempts),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Datos del niño o niña", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    ValidatedTextField(
                        field = state.childName,
                        onValueChange = viewModel::onChildNameChange,
                        label = "Nombre",
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
                        onImeAction = viewModel::save
                    )
                    LoadingButton("Guardar cambios", onClick = viewModel::save)
                }
            }

            OutlinedButton(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Text("  Cerrar sesión")
            }
        }
    }
}
