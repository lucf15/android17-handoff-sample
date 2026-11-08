package it.lucf15.ui.reader

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.lucf15.domain.model.Article
import it.lucf15.domain.model.Reading
import it.lucf15.ui.components.*
import it.lucf15.ui.theme.ReaderTheme

@Composable
internal fun ContinueBanner(
    reading: Reading,
    article: Article,
    backdrop: GlassBackdrop,
    accept: () -> Unit,
    dismiss: () -> Unit,
) {
    GlassSurface(
        modifier = Modifier.widthIn(max = 460.dp).fillMaxWidth(),
        backdrop = backdrop,
        shape = RoundedCornerShape(32.dp),
    ) {
        Column(Modifier.padding(24.dp)) {
            Text(
                "FROM YOUR OTHER DEVICE",
                color = ReaderTheme.colors.primary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text("Continue reading?", style = ReaderTheme.typography.headlineMedium)
            Spacer(Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ArticleArtwork(article.art, Modifier.size(72.dp).clip(RoundedCornerShape(18.dp)))
                Column(Modifier.weight(1f)) {
                    Text(article.title, style = ReaderTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${article.source} · Section ${reading.section + 1} of ${article.sections.size}",
                        fontSize = 12.sp,
                        color = ReaderTheme.colors.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = dismiss, modifier = Modifier.weight(1f), primary = false) {
                    Text("Not now")
                }
                Button(onClick = accept, modifier = Modifier.weight(1f)) {
                    Text("Continue")
                }
            }
        }
    }
}
