package com.example.lumihour.viewmodel

import PriceUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class MainViewModel : ViewModel() {

    // 1. Estados para el cálculo
    private val _kwhInput = MutableStateFlow("")
    val kwhInput: StateFlow<String> = _kwhInput

    // TODO: reemplazar API
    private val apiToken = BuildConfig.ESIOS_API_KEY


    private val _estimatedCost = MutableStateFlow<Double?>(null)
    val estimatedCost: StateFlow<Double?> = _estimatedCost

    // 2. Función para actualizar el texto de entrada
    fun onKwhInputChange(text: String) {
        _kwhInput.value = text
        // Borramos el cálculo anterior si se cambia el valor
        _estimatedCost.value = null
    }

    // 3. Función para realizar el cálculo
    fun calculateCost() {
        val kwh = _kwhInput.value.toDoubleOrNull()
        val currentState = _uiState.value

        // Solo calculamos si tenemos los datos y la entrada es válida
        if (kwh != null && currentState is PriceUiState.Success) {
            // El precio de la API viene en €/MWh, lo pasamos a €/kWh dividiendo por 1000
            val pricePerKwh = currentState.currentPrice / 1000
            _estimatedCost.value = pricePerKwh * kwh
        }
    }

    // Flujo de datos privado que podemos modificar
    private val _uiState = MutableStateFlow<PriceUiState>(PriceUiState.Loading)
    // Flujo público que la UI puede observar, pero no modificar
    val uiState: StateFlow<PriceUiState> = _uiState




    init {
        // Llamamos a la función para obtener los precios en cuanto se crea el ViewModel
        fetchPricesForToday()
    }

    fun fetchPricesForToday() {
        // Usamos el scope del ViewModel para lanzar una corrutina segura
        viewModelScope.launch {
            _uiState.value = PriceUiState.Loading // Ponemos el estado en "cargando"
            try {
                // Obtenemos la fecha en el formato que pide la API ("AAAA-MM-DD")
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

                // Llamamos a la API a través de Retrofit
                val response = RetrofitClient.instance.getElectricityPrices(
                    token = apiToken,
                    startDate = today,
                    endDate = today
                )

                if (response.isSuccessful && response.body() != null) {
                    val prices = response.body()!!.indicator.values

                    if (prices.isNotEmpty()) {
                        // Procesamos los datos
                        val cheapestHour = prices.minByOrNull { it.value }!!
                        val mostExpensiveHour = prices.maxByOrNull { it.value }!!

                        // Buscamos el precio de la hora actual
                        val currentHour = LocalTime.now().hour
                        val currentPriceData = prices.firstOrNull { priceValue ->
                            // El campo 'datetime' viene en formato UTC, lo convertimos para comparar horas
                            ZonedDateTime.parse(priceValue.datetime).hour == currentHour
                        }

                        // Actualizamos el estado a "Éxito" con los datos procesados
                        _uiState.value = PriceUiState.Success(
                            currentPrice = currentPriceData?.value ?: 0.0,
                            cheapestHour = cheapestHour,
                            mostExpensiveHour = mostExpensiveHour
                        )
                    } else {
                        _uiState.value = PriceUiState.Error("No se encontraron precios para hoy.")
                    }
                } else {
                    // Si la respuesta no fue exitosa, mostramos un error
                    _uiState.value = PriceUiState.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                // Si hubo un problema de conexión o de otro tipo
                _uiState.value = PriceUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
