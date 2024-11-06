package com.sensor.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sensor.app.presentation.history.HistoryRoute
import com.sensor.app.presentation.history.HistoryScreen
import com.sensor.app.presentation.main.MainRoute
import com.sensor.app.presentation.main.MainScreen
import com.sensor.app.presentation.record.RecordRoute
import com.sensor.app.presentation.record.RecordScreen
import com.sensor.app.ui.theme.SensorUsageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startDestination =MainRoute

        enableEdgeToEdge()
        setContent {
            SensorUsageTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ){
                    composable<MainRoute> {
                        MainScreen(
                            navController = navController
                        )
                    }

                    composable<RecordRoute> {
                        RecordScreen(
                            navController = navController
                        )
                    }

                    composable<HistoryRoute> {
                        HistoryScreen(
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}