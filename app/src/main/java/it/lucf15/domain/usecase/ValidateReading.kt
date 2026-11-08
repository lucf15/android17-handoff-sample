package it.lucf15.domain.usecase

import it.lucf15.domain.model.Reading
import it.lucf15.domain.repository.ArticleRepository

class ValidateReading(private val articles: ArticleRepository) {
    operator fun invoke(id: String?, section: Int, session: String?): Reading? {
        val article = articles.find(id) ?: return null
        if (section !in article.sections.indices || session.isNullOrBlank() || session.length > 64)
            return null
        return Reading(article.id, section, session)
    }
}
