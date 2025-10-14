import androidx.compose.runtime.Composable

@Composable
fun MainScreen(
uiState: PriceUiState,
kwhInput: String,
estimatedCost: Double?,
onKwhChange: (String) -> Unit,
onCalculateClick: () -> Unit
) {
    when (uiState) {
        is PriceUiState.Loading -> {
           // pantalla de carga
            LoadingScreen()
        }
        is PriceUiState.Success -> {
            // pantalla con los datos de precios
            PriceInfoScreen(
                currentPrice = uiState.currentPrice,
                cheapestHour = uiState.cheapestHour.datetime,
                cheapestPrice = uiState.cheapestHour.value,
                mostExpensiveHour = uiState.mostExpensiveHour.datetime,
                mostExpensivePrice = uiState.mostExpensiveHour.value,
                kwhInput = kwhInput,
                estimatedCost = estimatedCost,
                onKwhChange = onKwhChange,
                onCalculateClick = onCalculateClick
            )
        }
        is PriceUiState.Error -> {
             // mensaje de error
            ErrorScreen(message = uiState.message)
        }
    }
}
