package com.anirudha.alphabetlauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.anirudha.alphabetlauncher.ui.theme.DarkBg
import com.anirudha.alphabetlauncher.viewmodel.LauncherViewModel

/**
 * Main LauncherScreen composable layout binding state and UI components.
 */
@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val allApps by viewModel.allApps.collectAsState()
    val filteredApps by viewModel.filteredApps.collectAsState()
    val selectedLetter by viewModel.selectedLetter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var isDraggingAlphabet by remember { mutableStateOf(false) }
    var touchYPx by remember { mutableFloatStateOf(0f) }
    var activeTouchLetter by remember { mutableStateOf<Char?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkBg,
                        Color(0xFF0F172A),
                        Color(0xFF030712)
                    )
                )
            )
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Header with clock and date
            ClockHeader(
                selectedLetter = selectedLetter,
                totalAppsCount = allApps.size,
                filteredAppsCount = filteredApps.size,
                onClearFilter = { viewModel.clearFilter() }
            )

            // App grid list
            Row(modifier = Modifier.weight(1.0f)) {
                AppList(
                    apps = filteredApps,
                    selectedLetter = selectedLetter,
                    searchQuery = searchQuery,
                    isLoading = isLoading,
                    onSearchQueryChange = { query: String -> viewModel.updateSearchQuery(query) },
                    onAppClick = { app -> viewModel.launchApp(context, app) },
                    modifier = Modifier.weight(1.0f)
                )

                // A-Z Vertical Sidebar
                AlphabetBar(
                    selectedLetter = selectedLetter,
                    onLetterSelected = { letter: Char ->
                        viewModel.selectLetter(letter)
                    },
                    onTouchStateChanged = { isDragging: Boolean, yPx: Float, letter: Char? ->
                        isDraggingAlphabet = isDragging
                        touchYPx = yPx
                        if (letter != null) {
                            activeTouchLetter = letter
                        }
                    },
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }

        // Selected Letter Floating Bubble
        SelectedLetterBubble(
            letter = activeTouchLetter ?: selectedLetter,
            isVisible = isDraggingAlphabet,
            touchYPx = touchYPx,
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 64.dp)
        )
    }
}
