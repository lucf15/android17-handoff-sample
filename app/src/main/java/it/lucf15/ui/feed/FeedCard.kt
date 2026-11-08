package it.lucf15.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import it.lucf15.domain.model.Article
import it.lucf15.ui.components.*
import it.lucf15.ui.theme.ReaderTheme

@Composable
internal fun FeedCard(article: Article, expanded: Boolean, open: () -> Unit) {
    SoftSurface(
        Modifier.fillMaxWidth(),
        onClick = open,
        padding = PaddingValues(if (expanded) 12.dp else 18.dp),
    ) {
        if (expanded) {
            ArticleArtwork(
                article.art,
                Modifier.fillMaxWidth().height(184.dp).clip(RoundedCornerShape(20.dp)),
            )
            Column(Modifier.padding(10.dp)) {
                Text(
                    article.category.uppercase(),
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = ReaderTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(9.dp))
                Text(
                    article.title,
                    fontSize = 23.sp,
                    lineHeight = 29.sp,
                    fontWeight = FontWeight.SemiBold,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    article.summary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = ReaderTheme.colors.onSurfaceVariant,
                )
                Spacer(Modifier.height(18.dp))
                Metadata(article)
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        article.category.uppercase(),
                        fontSize = 10.sp,
                        letterSpacing = .8.sp,
                        color = ReaderTheme.colors.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        article.title,
                        fontSize = 17.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(12.dp))
                    Metadata(article)
                }
                ArticleArtwork(article.art, Modifier.size(94.dp).clip(RoundedCornerShape(20.dp)))
            }
        }
    }
}

@Composable
private fun Metadata(article: Article) {
    Text(
        "${article.source}  ·  ${article.minutes} min read",
        fontSize = 12.sp,
        color = ReaderTheme.colors.onSurfaceVariant,
    )
}
