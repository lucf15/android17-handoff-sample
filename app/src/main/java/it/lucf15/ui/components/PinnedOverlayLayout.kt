package it.lucf15.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.lucf15.ui.theme.ReaderTheme

@Composable
internal fun PinnedOverlayLayout(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    body: @Composable (topInset: Dp) -> Unit,
    footer: @Composable () -> Unit,
) {
    val background = ReaderTheme.colors.background
    SubcomposeLayout(modifier) { constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val top = subcompose("header", header).map { it.measure(loose) }
        val topHeight = top.maxOfOrNull { it.height } ?: 0
        val content = subcompose("body") { body(topHeight.toDp()) }.map { it.measure(constraints) }
        val bottom = subcompose("footer", footer).map { it.measure(loose) }
        val fadeHeight =
            ((bottom.maxOfOrNull { it.height } ?: 0) + 48.dp.roundToPx()).coerceAtMost(
                constraints.maxHeight,
            )
        val fade =
            subcompose("bottom-fade") {
                    Box(
                        Modifier.background(
                            Brush.verticalGradient(
                                listOf(
                                    background.copy(alpha = 0f),
                                    background.copy(alpha = .88f),
                                    background,
                                ),
                            ),
                        ),
                    )
                }
                .map { it.measure(Constraints.fixed(constraints.maxWidth, fadeHeight)) }
        layout(constraints.maxWidth, constraints.maxHeight) {
            content.forEach { it.place(0, 0) }
            fade.forEach { it.place(0, constraints.maxHeight - it.height) }
            top.forEach { it.place(0, 0) }
            bottom.forEach {
                it.place((constraints.maxWidth - it.width) / 2, constraints.maxHeight - it.height)
            }
        }
    }
}
