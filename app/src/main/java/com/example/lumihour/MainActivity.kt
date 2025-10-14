package com.example.lumihour

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.lumihour.ui.theme.LumiHourTheme
import com.example.lumihour.viewmodel.MainViewModel
import com.example.lumihour.ui.MainScreen  // <-- ESTE IMPORT FALTABA

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LumiHourTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val uiState by mainViewModel.uiState.collectAsState()
                    val kwhInput by mainViewModel.kwhInput.collectAsState()
                    val estimatedCost by mainViewModel.estimatedCost.collectAsState()

                    MainScreen(
                        uiState = uiState,
                        kwhInput = kwhInput,
                        estimatedCost = estimatedCost,
                        onKwhChange = mainViewModel::onKwhInputChange,
                        onCalculateClick = mainViewModel::calculateCost
                    )
                }
            }
        }
    }
}