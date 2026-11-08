package it.lucf15.domain.repository

import it.lucf15.domain.model.Library
import kotlinx.coroutines.flow.StateFlow

interface LibraryRepository {
    val library: StateFlow<Library>

    suspend fun toggleBookmark(id: String)

    suspend fun recordReading(id: String)

    suspend fun setFontSize(size: Int)
}
