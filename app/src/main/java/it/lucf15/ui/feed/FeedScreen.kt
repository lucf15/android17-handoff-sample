package it.lucf15.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import it.lucf15.domain.model.Article
import it.lucf15.ui.components.*
import it.lucf15.ui.theme.ReaderTheme

@Composable
internal fun FeedScreen(
    state: FeedContract.State,
    onEvent: (FeedContract.Event) -> Unit,
    open: (Article) -> Unit,
) {
    val grid = rememberLazyGridState()
    val backdrop = rememberGlassBackdrop()
    val focus = LocalFocusManager.current
    val labels = listOf("For you", "History", "Saved")
    val categories = listOf("All", "Design", "Technology", "Travel", "Living")
    val background = ReaderTheme.colors.background
    BoxWithConstraints(
        Modifier.fillMaxSize()
            .background(background)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
    ) {
        val wide = maxWidth >= 700.dp
        val columns =
            when {
                maxWidth >= 1100.dp -> 3
                wide -> 2
                else -> 1
            }
        val margin = if (wide) 32.dp else 20.dp
        val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        Box(Modifier.widthIn(max = 1200.dp).fillMaxSize().align(Alignment.TopCenter)) {
            PinnedOverlayLayout(
                Modifier.fillMaxSize(),
                header = {
                    Column(
                        Modifier.fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        background,
                                        background.copy(alpha = .94f),
                                        background.copy(alpha = 0f),
                                    ),
                                ),
                            )
                            .statusBarsPadding()
                            .padding(horizontal = margin)
                            .padding(top = 16.dp, bottom = 22.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                labels[state.tab],
                                modifier = Modifier.weight(1f),
                                style = ReaderTheme.typography.headlineLarge,
                            )
                            GlassSurface(
                                Modifier.size(48.dp),
                                backdrop,
                                onClick = { onEvent(FeedContract.Event.ToggleSearch) },
                            ) {
                                Icon(
                                    if (state.searching) Icons.Outlined.Close
                                    else Icons.Outlined.Search,
                                    if (state.searching) "Close search" else "Search articles",
                                    Modifier.size(24.dp),
                                )
                            }
                        }
                        if (state.searching) {
                            GlassSurface(Modifier.fillMaxWidth(), backdrop) {
                                SearchField(
                                    state.query,
                                    { onEvent(FeedContract.Event.Search(it)) },
                                    { focus.clearFocus() },
                                )
                            }
                        }
                        Box(Modifier.fillMaxWidth()) {
                            GlassSurface(Modifier.widthIn(max = 680.dp).fillMaxWidth(), backdrop) {
                                ReaderTabs(
                                    listOf("All", "Design", "Tech", "Travel", "Living"),
                                    categories.indexOf(state.category).coerceAtLeast(0),
                                    { onEvent(FeedContract.Event.SelectCategory(categories[it])) },
                                    Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                },
                body = { topInset ->
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        state = grid,
                        modifier = Modifier.fillMaxSize().captureBackdrop(backdrop),
                        contentPadding =
                            PaddingValues(
                                start = margin,
                                end = margin,
                                top = topInset + 4.dp,
                                bottom = bottomInset + 112.dp,
                            ),
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        item(key = "section", span = { GridItemSpan(maxLineSpan) }) {
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    if (state.tab == 0) "Today’s stories" else "Your collection",
                                    style = ReaderTheme.typography.labelLarge,
                                    color = ReaderTheme.colors.onSurfaceVariant,
                                )
                                Text(
                                    "${state.articles.size} stories",
                                    style = ReaderTheme.typography.labelSmall,
                                    color = ReaderTheme.colors.onSurfaceVariant,
                                )
                            }
                        }
                        items(state.articles, key = { it.id }, contentType = { "article" }) {
                            article ->
                            FeedCard(article, wide) { open(article) }
                        }
                        if (state.articles.isEmpty())
                            item(key = "empty", span = { GridItemSpan(maxLineSpan) }) {
                                SoftSurface(
                                    Modifier.fillMaxWidth(),
                                    padding = PaddingValues(28.dp),
                                ) {
                                    Text(
                                        "No stories here yet",
                                        style = ReaderTheme.typography.titleLarge,
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        if (state.tab == 2) "Save a story to keep it here."
                                        else "Try a different category or search.",
                                        color = ReaderTheme.colors.onSurfaceVariant,
                                    )
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
                            Modifier.padding(6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            val icons =
                                listOf(
                                    Icons.AutoMirrored.Outlined.Article,
                                    Icons.Outlined.History,
                                    Icons.Outlined.BookmarkBorder,
                                )
                            labels.forEachIndexed { index, label ->
                                val selected = state.tab == index
                                Column(
                                    Modifier.weight(1f)
                                        .clip(CircleShape)
                                        .background(
                                            if (selected)
                                                ReaderTheme.colors.onSurface.copy(alpha = .07f)
                                            else Color.Transparent,
                                        )
                                        .selectable(
                                            selected,
                                            role = Role.Tab,
                                            onClick = {
                                                onEvent(FeedContract.Event.SelectTab(index))
                                            },
                                        )
                                        .padding(vertical = 9.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(3.dp),
                                ) {
                                    Icon(
                                        icons[index],
                                        null,
                                        Modifier.size(23.dp),
                                        tint =
                                            if (selected) ReaderTheme.colors.onSurface
                                            else ReaderTheme.colors.onSurfaceVariant,
                                    )
                                    Text(
                                        label,
                                        style = ReaderTheme.typography.labelSmall,
                                        fontWeight =
                                            if (selected) FontWeight.SemiBold
                                            else FontWeight.Normal,
                                        color =
                                            if (selected) ReaderTheme.colors.onSurface
                                            else ReaderTheme.colors.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                },
            )
        }
    }
}
