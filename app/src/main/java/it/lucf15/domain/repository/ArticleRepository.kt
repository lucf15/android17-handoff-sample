package it.lucf15.domain.repository

import it.lucf15.domain.model.Article

interface ArticleRepository {
    fun articles(): List<Article>

    fun find(id: String?): Article?
}
