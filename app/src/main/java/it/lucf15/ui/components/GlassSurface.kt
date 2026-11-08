package it.lucf15.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import it.lucf15.ui.theme.ReaderTheme

@Composable
internal fun GlassSurface(
    modifier: Modifier = Modifier,
    backdrop: GlassBackdrop? = null,
    onClick: (() -> Unit)? = null,
    shape: Shape = CircleShape,
    content: @Composable BoxScope.() -> Unit,
) {
    val dark = ReaderTheme.colors.dark
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale =
        animateFloatAsState(if (pressed) .94f else 1f, spring(.65f, 650f), label = "glass-press")
    val fill =
        if (dark) listOf(Color(0xEC4A505B), Color(0xE6414650))
        else listOf(Color(0xE6FFFFFF), Color(0xCDEFF0F3))
    val rim =
        if (dark) listOf(Color.White.copy(.26f), Color.White.copy(.03f), Color.White.copy(.13f))
        else listOf(Color.White.copy(.85f), Color.White.copy(.25f), Color.Black.copy(.09f))
    Box(
        modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .shadow(
                16.dp,
                shape,
                clip = false,
                ambientColor = Color.Black.copy(.3f),
                spotColor = Color.Black.copy(.3f),
            )
            .clip(shape)
            .then(if (backdrop != null) Modifier.glassBackdrop(backdrop) else Modifier)
            .then(
                if (backdrop == null) Modifier.background(Brush.verticalGradient(fill))
                else Modifier,
            )
            .border(if (dark) .8.dp else .65.dp, Brush.verticalGradient(rim), shape)
            .then(
                if (onClick != null)
                    Modifier.clickable(
                        interactionSource = interaction,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick,
                    )
                else Modifier,
            ),
        contentAlignment = Alignment.Center,
        content = content,
    )
}
