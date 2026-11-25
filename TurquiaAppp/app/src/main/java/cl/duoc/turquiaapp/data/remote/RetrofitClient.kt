package cl.duoc.turquiaapp.data.remote

import android.os.Build
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val isEmulator: Boolean = (
            Build.FINGERPRINT.startsWith("generic") ||
                    Build.FINGERPRINT.startsWith("unknown") ||
                    Build.MODEL.contains("google_sdk") ||
                    Build.MODEL.contains("Emulator") ||
                    Build.MODEL.contains("Android SDK built for x86") ||
                    Build.HARDWARE.contains("goldfish") ||
                    Build.HARDWARE.contains("ranchu")
            )

    private val BASE_URL = if (isEmulator) {
        "http://10.0.2.2:8080/" // Emulador del PC
    } else {
        "http://localhost:8080/" // Celular físico por cable USB
    }

    val api: TurquiaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TurquiaApiService::class.java)
    }
}