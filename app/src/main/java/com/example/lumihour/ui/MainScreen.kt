package com.example.lumihour.ui

import androidx.compose.runtime.Composable
import com.example.lumihour.viewmodel.PriceUiState

@Composable
fun MainScreen(
    uiState: PriceUiState,
    kwhInput: String,
    estimatedCost: Double?,
    onKwhChange: (String) -> Unit,
    onCalculateClick: () -> Unit
) {
    // Usamos 'when' para reaccionar a los diferentes estados
    when (uiState) {
        is PriceUiState.Loading -> {
            // Muestra una pantalla de carga
            LoadingScreen()
        }
        is PriceUiState.Success -> {
            // Muestra la pantalla con los datos de precios
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
            // Muestra un mensaje de error
            ErrorScreen(message = uiState.message)
        }
    }
}