package com.example.lumihour.model

class PriceData {
}
// respuesta completa de la API
data class PriceResponse(
    val indicator: Indicator
)

// detalles del indicador, lista de valores horarios
data class Indicator(
    val name: String,
    val values: List<PriceValue>
)

// precio en un momento específico
data class PriceValue(
    val value: Double,
    val datetime: String // Formato "AAAA-MM-DDTHH:MM:SS"
)
