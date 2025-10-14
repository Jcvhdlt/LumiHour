package com.example.lumihour.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

// Colores según el roadmap
val GreenLight = Color(0xFFC8E6C9)
val OrangeLight = Color(0xFFFFE0B2)
val RedLight = Color(0xFFFFCDD2)

// --- Pantalla principal con la información ---
@Composable
fun PriceInfoScreen(
    currentPrice: Double,
    cheapestHour: String,
    cheapestPrice: Double,
    mostExpensiveHour: String,
    mostExpensivePrice: Double,
    kwhInput: String,
    estimatedCost: Double?,
    onKwhChange: (String) -> Unit,
    onCalculateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Columna para los elementos de precio
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PriceCard(
                label = "Precio Actual",
                price = currentPrice,
                backgroundColor = OrangeLight
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoBox(
                    label = "Hora más Barata",
                    hour = formatHour(cheapestHour),
                    price = cheapestPrice,
                    backgroundColor = GreenLight,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                InfoBox(
                    label = "Hora más Cara",
                    hour = formatHour(mostExpensiveHour),
                    price = mostExpensivePrice,
                    backgroundColor = RedLight,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Añadimos la nueva tarjeta de estimación
        EstimationCard(
            kwhInput = kwhInput,
            estimatedCost = estimatedCost,
            onKwhChange = onKwhChange,
            onCalculateClick = onCalculateClick
        )
    }
}

// --- Componentes Reutilizables ---

@Composable
fun PriceCard(label: String, price: Double, backgroundColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontSize = 20.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${String.format("%.4f", price / 1000)} €/kWh",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun InfoBox(label: String, hour: String, price: Double, backgroundColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontSize = 16.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = hour, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "${String.format("%.4f", price / 1000)} €/kWh", fontSize = 14.sp, color = Color.DarkGray)
    }
}

@Composable
fun EstimationCard(
    kwhInput: String,
    estimatedCost: Double?,
    onKwhChange: (String) -> Unit,
    onCalculateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF0F0F0)) // Un color de fondo neutro
            .padding(16.dp)
    ) {
        Text(
            text = "Estima tu Gasto",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para que el usuario introduzca los kWh
        OutlinedTextField(
            value = kwhInput,
            onValueChange = onKwhChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Consumo del aparato en kWh") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Botón para iniciar el cálculo
        Button(
            onClick = onCalculateClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calcular Coste por 1 Hora")
        }

        // Mostramos el resultado si ya se ha calculado
        estimatedCost?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Coste estimado: ${String.format("%.3f", it)} €",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// --- Pantallas de Estado ---

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Text(
            text = "Ha ocurrido un error:\n$message",
            color = Color.Red,
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
    }
}

// --- Función de ayuda ---

private fun formatHour(dateTimeString: String): String {
    return try {
        val zonedDateTime = ZonedDateTime.parse(dateTimeString)
        zonedDateTime.format(DateTimeFormatter.ofPattern("HH:00"))
    } catch (e: Exception) {
        "--:--"
    }
}