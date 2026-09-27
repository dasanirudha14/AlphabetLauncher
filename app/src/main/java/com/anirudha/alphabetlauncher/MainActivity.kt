package com.anirudha.alphabetlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.anirudha.alphabetlauncher.ui.LauncherScreen
import com.anirudha.alphabetlauncher.ui.theme.AlphabetLauncherTheme
import com.anirudha.alphabetlauncher.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            AlphabetLauncherTheme {
                LauncherScreen(viewModel = viewModel)
            }
        }
    }
}
