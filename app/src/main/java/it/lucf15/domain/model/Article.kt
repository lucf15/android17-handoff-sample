package it.lucf15.domain.model

data class Article(
    val id: String,
    val source: String,
    val category: String,
    val title: String,
    val summary: String,
    val minutes: Int,
    val art: Int,
    val sections: List<Section>,
)

data class Section(val title: String, val text: String)
