package it.lucf15.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import it.lucf15.ui.components.*
import it.lucf15.ui.theme.ReaderTheme

@Composable
internal fun ReaderTabs(
    labels: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dark = isSystemInDarkTheme()
    val position =
        animateFloatAsState(selected.toFloat(), spring(.75f, 450f), label = "selected-tab")
    val current by rememberUpdatedState(selected)
    val callback by rememberUpdatedState(onSelect)
    var preview by remember { mutableIntStateOf(-1) }
    val indicator = if (dark) Color(0xFF636B76) else Color.White.copy(alpha = .9f)
    BoxWithConstraints(modifier) {
        val width = maxWidth
        Box {
            Row(
                Modifier.width(width)
                    .heightIn(min = 48.dp)
                    .height(48.dp)
                    .clip(CircleShape)
                    .selectableGroup()
                    .pointerInput(labels) {
                        var dragging = false
                        detectHorizontalDragGestures(
                            onDragStart = { at ->
                                val slot =
                                    ((at.x - 4.dp.toPx()) /
                                            ((size.width - 8.dp.toPx()) / labels.size))
                                        .toInt()
                                        .coerceIn(labels.indices)
                                dragging = slot == current
                                if (dragging) preview = current
                            },
                            onDragEnd = {
                                if (dragging && preview >= 0) callback(preview)
                                preview = -1
                                dragging = false
                            },
                            onDragCancel = {
                                preview = -1
                                dragging = false
                            },
                        ) { change, _ ->
                            if (dragging) {
                                change.consume()
                                preview =
                                    ((change.position.x - 4.dp.toPx()) /
                                            ((size.width - 8.dp.toPx()) / labels.size))
                                        .toInt()
                                        .coerceIn(labels.indices)
                            }
                        }
                    }
                    .drawBehind {
                        val inset = 4.dp.toPx()
                        val cell = (size.width - 2 * inset) / labels.size
                        val x =
                            inset + cell * if (preview >= 0) preview.toFloat() else position.value
                        drawRoundRect(
                            indicator,
                            Offset(x, inset),
                            Size(cell, size.height - inset * 2),
                            CornerRadius(size.height / 2),
                        )
                    }
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                labels.forEachIndexed { index, label ->
                    Box(
                        Modifier.weight(1f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .selectable(
                                selected == index,
                                role = Role.Tab,
                                onClick = { onSelect(index) },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            label,
                            fontSize = 12.sp,
                            fontWeight =
                                if (selected == index) FontWeight.SemiBold else FontWeight.Normal,
                            color =
                                if (selected == index) ReaderTheme.colors.onSurface
                                else ReaderTheme.colors.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
