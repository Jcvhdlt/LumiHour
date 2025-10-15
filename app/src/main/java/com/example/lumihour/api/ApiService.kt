import com.example.lumihour.model.PriceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    @GET("datos/mercados/precios-mercados-tiempo-real")
    suspend fun getElectricityPrices(
        @Header("x-api-key") apiKey: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("time_trunc") timeTrunc: String = "hour"
    ): Response<PriceResponse>

}
