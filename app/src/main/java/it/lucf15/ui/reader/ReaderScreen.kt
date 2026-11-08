package it.lucf15.ui.reader

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import it.lucf15.domain.model.Article
import it.lucf15.ui.components.*
import it.lucf15.ui.theme.ReaderTheme
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
internal fun ReaderScreen(
    article: Article,
    state: ReaderContract.State,
    onEvent: (ReaderContract.Event) -> Unit,
    back: () -> Unit,
) {
    val initialIndex =
        remember(article.id, state.revision) { if (state.section == 0) 0 else state.section + 1 }
    val list =
        key(article.id, state.revision) {
            rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
        }
    val backdrop = rememberGlassBackdrop()
    val background = ReaderTheme.colors.background
    LaunchedEffect(list, article.id) {
        snapshotFlow { (list.firstVisibleItemIndex - 1).coerceAtLeast(0) }
            .distinctUntilChanged()
            .collect {
                if (state.article?.id == article.id) onEvent(ReaderContract.Event.Position(it))
            }
    }
    BoxWithConstraints(
        Modifier.fillMaxSize()
            .background(background)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
    ) {
        val wide = maxWidth >= 700.dp
        val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        PinnedOverlayLayout(
            Modifier.fillMaxSize(),
            header = {
                Row(
                    Modifier.fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(background.copy(alpha = .95f), background.copy(alpha = 0f)),
                            ),
                        )
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    GlassSurface(Modifier.size(48.dp), backdrop, back) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            "Back to feed",
                            Modifier.size(23.dp),
                        )
                    }
                    GlassSurface(backdrop = backdrop) {
                        Text(
                            article.source,
                            Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                            style = ReaderTheme.typography.labelLarge,
                        )
                    }
                    Spacer(Modifier.size(48.dp))
                }
            },
            body = { topInset ->
                Box(
                    Modifier.fillMaxSize().captureBackdrop(backdrop),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    LazyColumn(
                        state = list,
                        modifier = Modifier.widthIn(max = 760.dp).fillMaxSize(),
                        contentPadding =
                            PaddingValues(
                                start = if (wide) 36.dp else 24.dp,
                                end = if (wide) 36.dp else 24.dp,
                                top = topInset + 16.dp,
                                bottom = bottomInset + 112.dp,
                            ),
                        verticalArrangement = Arrangement.spacedBy(28.dp),
                    ) {
                        item(key = "heading") {
                            Column {
                                Text(
                                    article.category.uppercase(),
                                    style = ReaderTheme.typography.labelSmall,
                                    letterSpacing = 1.5.sp,
                                    color = ReaderTheme.colors.primary,
                                )
                                Spacer(Modifier.height(14.dp))
                                Text(
                                    article.title,
                                    style = ReaderTheme.typography.headlineLarge,
                                    fontSize = if (wide) 42.sp else 34.sp,
                                    lineHeight = if (wide) 49.sp else 40.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    article.summary,
                                    style = ReaderTheme.typography.bodyLarge,
                                    fontSize = 17.sp,
                                    lineHeight = 26.sp,
                                    color = ReaderTheme.colors.onSurfaceVariant,
                                )
                                Spacer(Modifier.height(18.dp))
                                Text(
                                    "${article.source}  ·  ${article.minutes} min read",
                                    style = ReaderTheme.typography.labelSmall,
                                    color = ReaderTheme.colors.onSurfaceVariant,
                                )
                                Spacer(Modifier.height(28.dp))
                                ArticleArtwork(
                                    article.art,
                                    Modifier.fillMaxWidth()
                                        .height(if (wide) 300.dp else 230.dp)
                                        .clip(RoundedCornerShape(24.dp)),
                                )
                            }
                        }
                        itemsIndexed(
                            article.sections,
                            key = { i, _ -> "section-$i" },
                            contentType = { _, _ -> "section" },
                        ) { _, section ->
                            Column {
                                Text(section.title, style = ReaderTheme.typography.titleLarge)
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    section.text,
                                    style = ReaderTheme.typography.bodyLarge,
                                    fontSize = state.fontSize.sp,
                                    lineHeight = (state.fontSize * 1.65).sp,
                                )
                            }
                        }
                        item(key = "end") {
                            Column(
                                Modifier.fillMaxWidth().fillParentMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    "You’re all caught up",
                                    style = ReaderTheme.typography.titleLarge,
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    "There’s another good story waiting.",
                                    color = ReaderTheme.colors.onSurfaceVariant,
                                )
                                Spacer(Modifier.height(24.dp))
                                Button(onClick = back) { Text("Back to feed") }
                            }
                        }
                    }
                }
            },
            footer = {
                GlassSurface(
                    Modifier.padding(bottom = bottomInset + 16.dp)
                        .widthIn(max = 360.dp)
                        .fillMaxWidth(.86f),
                    backdrop,
                ) {
                    Row(
                        Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = { onEvent(ReaderContract.Event.ResizeText) }) {
                            Text("Aa", fontSize = 19.sp, fontWeight = FontWeight.Medium)
                        }
                        Column(
                            Modifier.weight(1f).padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(7.dp),
                        ) {
                            Text(
                                "Section ${state.section + 1} of ${article.sections.size}",
                                style = ReaderTheme.typography.labelSmall,
                            )
                            val primary = ReaderTheme.colors.primary
                            val track = ReaderTheme.colors.outlineVariant
                            Canvas(
                                Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                            ) {
                                drawRect(track)
                                drawRect(
                                    primary,
                                    size =
                                        Size(
                                            size.width * (state.section + 1f) /
                                                article.sections.size,
                                            size.height,
                                        ),
                                )
                            }
                        }
                        IconButton(onClick = { onEvent(ReaderContract.Event.Bookmark) }) {
                            Icon(
                                if (state.saved) Icons.Outlined.Bookmark
                                else Icons.Outlined.BookmarkBorder,
                                if (state.saved) "Remove bookmark" else "Save article",
                                tint =
                                    if (state.saved) ReaderTheme.colors.primary
                                    else ReaderTheme.colors.onSurface,
                            )
                        }
                    }
                }
            },
        )
    }
}
