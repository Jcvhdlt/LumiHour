package com.example.lumihour.viewmodel

import PriceUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumihour.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MainViewModel : ViewModel() {

    // --- Estados para el cálculo ---
    private val _kwhInput = MutableStateFlow("")
    val kwhInput: StateFlow<String> = _kwhInput

    private val _estimatedCost = MutableStateFlow<Double?>(null)
    val estimatedCost: StateFlow<Double?> = _estimatedCost

    // --- Estado de la UI para los precios ---
    private val _uiState = MutableStateFlow<PriceUiState>(PriceUiState.Loading)
    val uiState: StateFlow<PriceUiState> = _uiState

    // Inicializa la carga de datos al crear el ViewModel
    init {
        fetchPricesForToday()
    }

    // --- Funciones para la interacción con la UI ---
    fun onKwhInputChange(text: String) {
        _kwhInput.value = text
        _estimatedCost.value = null
    }

    fun calculateCost() {
        val kwh = _kwhInput.value.toDoubleOrNull()
        val currentState = _uiState.value

        if (kwh != null && currentState is PriceUiState.Success) {
            val pricePerKwh = currentState.currentPrice / 1000
            _estimatedCost.value = pricePerKwh * kwh
        }
    }

    // --- Lógica de red ---
    fun fetchPricesForToday() {
        viewModelScope.launch {
            _uiState.value = PriceUiState.Loading
            try {
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

                // *** CORRECCIÓN CLAVE ***
                // Llama al método corregido con 'x-api-key'
                val response = RetrofitClient.instance.getElectricityPrices(
                    apiKey = BuildConfig.ESIOS_API_KEY, // Pasa la clave con el nombre de parámetro 'apiKey'
                    startDate = today,
                    endDate = today
                )

                if (response.isSuccessful && response.body() != null) {
                    val prices = response.body()!!.indicator.values

                    if (prices.isNotEmpty()) {
                        val cheapestHour = prices.minByOrNull { it.value }!!
                        val mostExpensiveHour = prices.maxByOrNull { it.value }!!

                        val currentHour = LocalTime.now().hour
                        val currentPriceData = prices.firstOrNull { priceValue ->
                            ZonedDateTime.parse(priceValue.datetime).hour == currentHour
                        }

                        _uiState.value = PriceUiState.Success(
                            currentPrice = currentPriceData?.value ?: 0.0,
                            cheapestHour = cheapestHour,
                            mostExpensiveHour = mostExpensiveHour
                        )
                    } else {
                        _uiState.value = PriceUiState.Error("No se encontraron precios para hoy.")
                    }
                } else {
                    _uiState.value = PriceUiState.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                _uiState.value = PriceUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
