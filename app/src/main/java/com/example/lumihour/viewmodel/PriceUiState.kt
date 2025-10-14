import com.example.lumihour.model.PriceValue

sealed interface PriceUiState {
    object Loading : PriceUiState
    data class Success(
        val currentPrice: Double,
        val cheapestHour: PriceValue,
        val mostExpensiveHour: PriceValue
    ) : PriceUiState
    data class Error(val message: String) : PriceUiState
}
