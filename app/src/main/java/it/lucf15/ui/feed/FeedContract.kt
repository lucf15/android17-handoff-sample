package it.lucf15.ui.feed

import androidx.compose.runtime.Stable
import it.lucf15.domain.model.Article

object FeedContract {
    @Stable
    interface State {
        val tab: Int
        val category: String
        val query: String
        val searching: Boolean
        val articles: List<Article>
    }

    sealed interface Event {
        data class SelectTab(val value: Int) : Event

        data class SelectCategory(val value: String) : Event

        data class Search(val value: String) : Event

        data object ToggleSearch : Event
    }
}
