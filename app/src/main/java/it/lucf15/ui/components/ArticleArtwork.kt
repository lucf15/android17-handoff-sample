package it.lucf15.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import it.lucf15.R

private val articleImages =
    listOf(
        R.drawable.article_0,
        R.drawable.article_1,
        R.drawable.article_2,
        R.drawable.article_3,
        R.drawable.article_4,
        R.drawable.article_5,
    )

@Composable
internal fun ArticleArtwork(kind: Int, modifier: Modifier) {
    AsyncImage(
        model = articleImages[kind.coerceIn(articleImages.indices)],
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}
