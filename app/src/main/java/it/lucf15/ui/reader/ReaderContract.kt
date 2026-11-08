package it.lucf15.ui.reader

import androidx.compose.runtime.Stable
import it.lucf15.domain.model.*

object ReaderContract {
    @Stable
    interface State {
        val article: Article?
        val section: Int
        val session: String
        val pending: Reading?
        val pendingArticle: Article?
        val saved: Boolean
        val fontSize: Int
        val notice: String?
        val revision: Int
    }

    sealed interface Event {
        data class Open(val articleId: String) : Event

        data class Position(val section: Int) : Event

        data class Receive(val reading: Reading) : Event

        data object Accept : Event

        data object Dismiss : Event

        data object Back : Event

        data object Bookmark : Event

        data object ResizeText : Event

        data object ClearNotice : Event
    }
}
