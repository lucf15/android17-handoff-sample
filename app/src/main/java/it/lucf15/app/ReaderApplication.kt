package it.lucf15.app

import android.app.Application
import it.lucf15.data.*
import it.lucf15.domain.repository.*
import kotlinx.coroutines.*

class ReaderApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val articles: ArticleRepository by lazy { LocalArticleRepository() }
    val library: LibraryRepository by lazy { PreferencesLibraryRepository(this, applicationScope) }
}
