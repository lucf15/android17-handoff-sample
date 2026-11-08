package it.lucf15.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.*
import it.lucf15.ui.components.*
import it.lucf15.ui.feed.*
import it.lucf15.ui.reader.*
import it.lucf15.ui.theme.*
import it.lucf15.ui.theme.ReaderTheme
import kotlinx.coroutines.delay

@Composable
internal fun ReaderApp(
    feed: FeedContract.State,
    reader: ReaderContract.State,
    onFeedEvent: (FeedContract.Event) -> Unit,
    onReaderEvent: (ReaderContract.Event) -> Unit,
) {
    ReaderTheme {
        val backdrop = rememberGlassBackdrop()
        BackHandler(reader.article != null || feed.searching) {
            if (feed.searching) onFeedEvent(FeedContract.Event.ToggleSearch)
            else onReaderEvent(ReaderContract.Event.Back)
        }
        Box(Modifier.fillMaxSize().background(ReaderTheme.colors.surface)) {
            AnimatedContent(
                targetState = reader.article,
                modifier = Modifier.fillMaxSize().clipToBounds().captureBackdrop(backdrop),
                transitionSpec = {
                    val timing =
                        tween<androidx.compose.ui.unit.IntOffset>(
                            360,
                            easing = CubicBezierEasing(.2f, 0f, 0f, 1f),
                        )
                    if (targetState != null) {
                        (slideInHorizontally(timing) { it } togetherWith
                                slideOutHorizontally(timing) { -(it * .3f).toInt() })
                            .apply { targetContentZIndex = 1f }
                            .using(null)
                    } else {
                        (slideInHorizontally(timing) { -(it * .3f).toInt() } togetherWith
                                slideOutHorizontally(timing) { it })
                            .apply { targetContentZIndex = -1f }
                            .using(null)
                    }
                },
                contentKey = { it?.id },
                label = "reader-navigation",
            ) { article ->
                if (article == null)
                    FeedScreen(feed, onFeedEvent) {
                        onReaderEvent(ReaderContract.Event.Open(it.id))
                    }
                else
                    ReaderScreen(article, reader, onReaderEvent) {
                        onReaderEvent(ReaderContract.Event.Back)
                    }
            }
            ReaderFeedback(
                reader,
                backdrop,
                onReaderEvent,
                Modifier.align(Alignment.BottomCenter).navigationBarsPadding(),
            )
        }
    }
}

@Composable
private fun ReaderFeedback(
    state: ReaderContract.State,
    backdrop: GlassBackdrop,
    onEvent: (ReaderContract.Event) -> Unit,
    modifier: Modifier,
) {
    val pending = state.pending
    val article = state.pendingArticle
    AnimatedVisibility(
        pending != null && article != null,
        modifier.padding(horizontal = 16.dp, vertical = 88.dp),
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut(),
    ) {
        if (pending != null && article != null)
            ContinueBanner(
                pending,
                article,
                backdrop,
                { onEvent(ReaderContract.Event.Accept) },
                { onEvent(ReaderContract.Event.Dismiss) },
            )
    }
    if (state.notice != null && pending == null) {
        LaunchedEffect(state.notice) {
            delay(4000)
            onEvent(ReaderContract.Event.ClearNotice)
        }
        GlassSurface(modifier.padding(20.dp), backdrop, shape = RoundedCornerShape(24.dp)) {
            Text(state.notice.orEmpty(), modifier = Modifier.padding(18.dp), fontSize = 14.sp)
        }
    }
}
