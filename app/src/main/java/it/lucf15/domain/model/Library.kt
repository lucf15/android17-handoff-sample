package it.lucf15.domain.model

data class Library(
    val saved: Set<String> = emptySet(),
    val history: List<String> = emptyList(),
    val fontSize: Int = 18,
)
