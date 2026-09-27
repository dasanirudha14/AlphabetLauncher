package com.anirudha.alphabetlauncher.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.anirudha.alphabetlauncher.data.AppInfo
import com.anirudha.alphabetlauncher.data.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel managing state and in-memory app filtering for Alphabet Launcher.
 */
class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application.applicationContext)

    // Cached full list of installed launchable apps
    private val _allApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val allApps: StateFlow<List<AppInfo>> = _allApps.asStateFlow()

    // Currently selected A-Z letter filter (null when showing all apps)
    private val _selectedLetter = MutableStateFlow<Char?>(null)
    val selectedLetter: StateFlow<Char?> = _selectedLetter.asStateFlow()

    // Search query text
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Loading indicator state
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Filtered apps state derived strictly in-memory from cached apps
    val filteredApps: StateFlow<List<AppInfo>> = combine(
        _allApps,
        _selectedLetter,
        _searchQuery
    ) { apps, letter, query ->
        var result = apps

        if (letter != null) {
            result = result.filter { app ->
                app.name.startsWith(letter, ignoreCase = true)
            }
        }

        if (query.isNotBlank()) {
            result = result.filter { app ->
                app.name.contains(query, ignoreCase = true)
            }
        }

        result
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadInstalledApps()
    }

    /**
     * Loads installed launchable apps ONCE from PackageManager on background thread.
     */
    fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            val apps = repository.getLaunchableApps()
            _allApps.value = apps
            _isLoading.value = false
        }
    }

    /**
     * Selects or toggles an A-Z letter filter.
     * Operates purely in-memory on cached application list.
     */
    fun selectLetter(letter: Char?) {
        if (_selectedLetter.value == letter) {
            // Tapping same letter again clears the filter
            _selectedLetter.value = null
        } else {
            _selectedLetter.value = letter
        }
    }

    /**
     * Clears letter selection and resets to default view showing all apps.
     */
    fun clearFilter() {
        _selectedLetter.value = null
        _searchQuery.value = ""
    }

    /**
     * Updates text search query.
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Safely launches an app intent with fallback error handling.
     */
    fun launchApp(context: Context, app: AppInfo) {
        try {
            val intent = app.launchIntent ?: context.packageManager.getLaunchIntentForPackage(app.packageName)
            if (intent != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(
                    context,
                    "Unable to launch ${app.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Error launching ${app.name}: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
