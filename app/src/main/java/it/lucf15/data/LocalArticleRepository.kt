package it.lucf15.data

import it.lucf15.domain.repository.ArticleRepository

class LocalArticleRepository : ArticleRepository {
    override fun articles() = SampleArticles.all

    override fun find(id: String?) = SampleArticles.find(id)
}
