package it.lucf15.ui.feed

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.lucf15.domain.model.*
import it.lucf15.domain.repository.*
import kotlinx.coroutines.launch

class FeedViewModel(private val catalog: ArticleRepository, library: LibraryRepository) :
    ViewModel() {
    private class MutableState : FeedContract.State {
        override var tab by mutableIntStateOf(0)
        override var category by mutableStateOf("All")
        override var query by mutableStateOf("")
        override var searching by mutableStateOf(false)
        override var articles by mutableStateOf<List<Article>>(emptyList())
    }

    private val mutableState = MutableState()
    val state: FeedContract.State = mutableState
    private var library = Library()

    init {
        viewModelScope.launch {
            library.library.collect { value ->
                this@FeedViewModel.library = value
                onRefresh()
            }
        }
    }

    fun onEvent(event: FeedContract.Event) = Snapshot.withMutableSnapshot {
        when (event) {
            is FeedContract.Event.SelectTab -> mutableState.tab = event.value.coerceIn(0, 2)
            is FeedContract.Event.SelectCategory -> mutableState.category = event.value
            is FeedContract.Event.Search -> mutableState.query = event.value
            FeedContract.Event.ToggleSearch -> {
                mutableState.searching = !state.searching
                if (!state.searching) mutableState.query = ""
            }
        }
        onRefresh()
    }

    private fun onRefresh() {
        val base =
            when (state.tab) {
                1 -> library.history.mapNotNull(catalog::find)
                2 -> catalog.articles().filter { it.id in library.saved }
                else -> catalog.articles()
            }
        mutableState.articles = base.filter {
            (state.category == "All" || it.category == state.category) &&
                (state.query.isBlank() || (it.title + it.source).contains(state.query, true))
        }
    }
}
