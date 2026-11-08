package it.lucf15.ui.reader

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.*
import it.lucf15.domain.model.*
import it.lucf15.domain.repository.*
import it.lucf15.domain.usecase.ValidateReading
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class ReaderViewModel(
    private val catalog: ArticleRepository,
    private val library: LibraryRepository,
    private val savedState: SavedStateHandle,
) : ViewModel() {
    private class MutableState : ReaderContract.State {
        override var article by mutableStateOf<Article?>(null)
        override var section by mutableIntStateOf(0)
        override var session by mutableStateOf(UUID.randomUUID().toString())
        override var pending by mutableStateOf<Reading?>(null)
        override var pendingArticle by mutableStateOf<Article?>(null)
        override var saved by mutableStateOf(false)
        override var fontSize by mutableIntStateOf(18)
        override var notice by mutableStateOf<String?>(null)
        override var revision by mutableIntStateOf(0)
    }

    private val mutableState = MutableState()
    val state: ReaderContract.State = mutableState
    private val validate = ValidateReading(catalog)

    init {
        validate(savedState["article"], savedState["section"] ?: 0, savedState["session"])?.let {
            onRestore(it)
        }
        validate(
                savedState["pendingArticle"],
                savedState["pendingSection"] ?: -1,
                savedState["pendingSession"],
            )
            ?.let {
                mutableState.pending = it
                mutableState.pendingArticle = catalog.find(it.articleId)
            }
        viewModelScope.launch {
            library.library.collect { value ->
                Snapshot.withMutableSnapshot {
                    mutableState.saved = state.article?.id in value.saved
                    mutableState.fontSize = value.fontSize
                }
            }
        }
    }

    fun onEvent(event: ReaderContract.Event) {
        Snapshot.withMutableSnapshot {
            when (event) {
                is ReaderContract.Event.Open ->
                    catalog.find(event.articleId)?.let {
                        onRestore(Reading(it.id, 0, UUID.randomUUID().toString()))
                        onRecord(it.id)
                    }
                is ReaderContract.Event.Position ->
                    mutableState.section =
                        event.section.coerceIn(0, state.article?.sections?.lastIndex ?: 0)
                is ReaderContract.Event.Receive ->
                    validate(event.reading.articleId, event.reading.section, event.reading.session)
                        ?.let {
                            mutableState.pending = it
                            mutableState.pendingArticle = catalog.find(it.articleId)
                        }
                ReaderContract.Event.Accept ->
                    state.pending?.let {
                        onRestore(it)
                        onRecord(it.articleId)
                        onClearPending()
                    }
                ReaderContract.Event.Dismiss -> onClearPending()
                ReaderContract.Event.Back -> mutableState.article = null
                ReaderContract.Event.Bookmark ->
                    state.article?.id?.let { id -> onPersist { library.toggleBookmark(id) } }
                ReaderContract.Event.ResizeText -> {
                    val size = if (state.fontSize >= 22) 18 else state.fontSize + 2
                    onPersist { library.setFontSize(size) }
                }
                ReaderContract.Event.ClearNotice -> mutableState.notice = null
            }
        }
        onSave()
    }

    fun capture(): Reading? = state.article?.let { Reading(it.id, state.section, state.session) }

    private fun onRestore(reading: Reading) {
        mutableState.article = catalog.find(reading.articleId)
        mutableState.section = reading.section
        mutableState.session = reading.session
        mutableState.saved = reading.articleId in library.library.value.saved
        mutableState.revision++
    }

    private fun onClearPending() {
        mutableState.pending = null
        mutableState.pendingArticle = null
    }

    private fun onRecord(id: String) = onPersist { library.recordReading(id) }

    private fun onPersist(action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                mutableState.notice = "Could not save your changes. Try again."
            }
        }
    }

    private fun onSave() {
        savedState["article"] = state.article?.id
        savedState["section"] = state.section
        savedState["session"] = state.session
        savedState["pendingArticle"] = state.pending?.articleId
        savedState["pendingSection"] = state.pending?.section
        savedState["pendingSession"] = state.pending?.session
    }
}
