package cl.duoc.turquiaapp.device

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Recurso nativo: vibración. Usa el servicio del sistema Vibrator
 * (requiere el permiso VIBRATE en el AndroidManifest, que no se pide en tiempo de ejecución).
 */
object VibrationSupport {

    /** Hace vibrar el teléfono [milliseconds] ms. Si el equipo no tiene vibrador, no hace nada. */
    fun vibrate(context: Context, milliseconds: Long = 80L) {
        val vibrator = getVibrator(context)
        if (!vibrator.hasVibrator()) return
        vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    private fun getVibrator(context: Context): Vibrator =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
}
