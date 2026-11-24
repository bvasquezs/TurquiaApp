package cl.duoc.turquiaapp.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cl.duoc.turquiaapp.ui.theme.SuccessGreen
import cl.duoc.turquiaapp.viewmodel.FieldState
import kotlin.math.roundToInt

/**
 * Campo de texto con retroalimentación visual:
 *  - borde y texto en rojo + ícono de error cuando [FieldState.error] != null
 *  - ícono ✓ verde cuando el valor es válido
 *  - mensaje de error animado debajo del campo
 * No contiene reglas de validación: solo muestra lo que decide el ViewModel.
 */
@Composable
fun ValidatedTextField(
    field: FieldState,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    helper: String? = null,
    extraTrailing: (@Composable () -> Unit)? = null,
    onImeAction: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val hasError = field.error != null

    OutlinedTextField(
        value = field.value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        isError = hasError,
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                extraTrailing?.invoke()
                if (hasError) {
                    Icon(
                        Icons.Filled.Error,
                        contentDescription = "Campo con error",
                        tint = MaterialTheme.colorScheme.error
                    )
                } else if (field.isValid) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = "Campo válido",
                        tint = SuccessGreen
                    )
                }
                if (isPassword) {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            }
        },
        supportingText = {
            AnimatedVisibility(
                visible = hasError || helper != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(field.error ?: helper.orEmpty())
            }
        },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation()
        else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            capitalization = capitalization,
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = { focusManager.clearFocus(); onImeAction() },
            onGo = { focusManager.clearFocus(); onImeAction() },
            onSearch = { focusManager.clearFocus(); onImeAction() }
        )
    )
}

/** Sacude el componente horizontalmente cada vez que [trigger] aumenta (intento de envío con errores). */
fun Modifier.shake(trigger: Int): Modifier = composed {
    val offsetX = remember { Animatable(0f) }
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            repeat(3) {
                offsetX.animateTo(14f, tween(45))
                offsetX.animateTo(-14f, tween(45))
            }
            offsetX.animateTo(0f, tween(45))
        }
    }
    this.offset { IntOffset(offsetX.value.roundToInt(), 0) }
}

/** Banner de error general del formulario (por ejemplo "Correo o contraseña incorrectos"). */
@Composable
fun ErrorBanner(message: String?, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = message != null,
        modifier = modifier,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Error, contentDescription = null)
                Text(message.orEmpty(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/** Botón principal que muestra un indicador de progreso mientras se procesa la acción. */
@Composable
fun LoadingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(text, style = MaterialTheme.typography.titleMedium)
        }
    }
}

/** Lleva al usuario a los ajustes de la app para que conceda un permiso denegado. */
fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
