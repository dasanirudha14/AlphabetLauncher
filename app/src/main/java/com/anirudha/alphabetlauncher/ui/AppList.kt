package com.anirudha.alphabetlauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anirudha.alphabetlauncher.data.AppInfo
import com.anirudha.alphabetlauncher.ui.theme.AccentCyan
import com.anirudha.alphabetlauncher.ui.theme.TextMuted
import com.anirudha.alphabetlauncher.ui.theme.TextPrimary
import com.anirudha.alphabetlauncher.ui.theme.TextSecondary

/**
 * AppList composable displaying the grid of filtered installed applications,
 * loading states, empty state messages, and search box.
 */
@Composable
fun AppList(
    apps: List<AppInfo>,
    selectedLetter: Char?,
    searchQuery: String,
    isLoading: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        
        // Search Input Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0x33334155))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🔍",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(modifier = Modifier.weight(1.0f)) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = if (selectedLetter != null) "Search in '${selectedLetter}' apps…" else "Search apps…",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            color = TextPrimary,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = AccentCyan,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Loading installed applications…",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            apps.isEmpty() -> {
                // Requirement 7: EMPTY STATE display when no apps begin with selected letter
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0x33334155)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = selectedLetter?.toString() ?: "🔍",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (selectedLetter != null) {
                                "No apps found for '$selectedLetter'"
                            } else if (searchQuery.isNotEmpty()) {
                                "No apps found for \"$searchQuery\""
                            } else {
                                "No installed apps found"
                            },
                            style = TextStyle(
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try selecting another letter or clearing the search.",
                            color = TextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                // 4 Column Grid of Applications
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        end = 12.dp,
                        top = 8.dp,
                        bottom = 80.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = apps,
                        key = { it.packageName }
                    ) { app ->
                        AppItem(
                            app = app,
                            onAppClick = onAppClick
                        )
                    }
                }
            }
        }
    }
}
