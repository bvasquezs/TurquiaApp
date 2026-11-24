package cl.duoc.turquiaapp.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TurquiaApiService {

    @GET("api/palabras")
    suspend fun obtenerTodas(): List<PalabraDto>

    @POST("api/palabras")
    suspend fun crearPalabra(@Body palabra: PalabraDto): PalabraDto

    @DELETE("api/palabras/{id}")
    suspend fun eliminarPalabra(@Path("id") id: Long)
}