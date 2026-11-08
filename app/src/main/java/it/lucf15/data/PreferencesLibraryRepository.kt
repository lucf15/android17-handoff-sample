package it.lucf15.data

import android.content.Context
import it.lucf15.domain.model.Library
import it.lucf15.domain.repository.LibraryRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PreferencesLibraryRepository(context: Context, scope: CoroutineScope) : LibraryRepository {
    private val appContext = context.applicationContext
    private val mutableLibrary = MutableStateFlow(Library())
    override val library = mutableLibrary.asStateFlow()
    private val mutex = Mutex()
    private val loaded =
        scope.async(Dispatchers.IO) {
            val prefs = appContext.getSharedPreferences("reader", Context.MODE_PRIVATE)
            mutableLibrary.value =
                Library(
                    prefs.getStringSet("saved", emptySet()).orEmpty().toSet(),
                    prefs.getString("history", "").orEmpty().split(',').filter(String::isNotBlank),
                    prefs.getInt("font", 18).coerceIn(16, 22),
                )
            prefs
        }

    override suspend fun toggleBookmark(id: String) = update { current ->
        current.copy(saved = if (id in current.saved) current.saved - id else current.saved + id)
    }

    override suspend fun recordReading(id: String) = update { current ->
        current.copy(history = listOf(id) + current.history.filterNot { it == id })
    }

    override suspend fun setFontSize(size: Int) = update {
        it.copy(fontSize = size.coerceIn(16, 22))
    }

    private suspend fun update(transform: (Library) -> Library) {
        val prefs = loaded.await()
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val next = transform(mutableLibrary.value)
                check(
                    prefs
                        .edit()
                        .putStringSet("saved", next.saved)
                        .putString("history", next.history.joinToString(","))
                        .putInt("font", next.fontSize)
                        .commit(),
                ) {
                    "Unable to save reader preferences"
                }
                mutableLibrary.value = next
            }
        }
    }
}
