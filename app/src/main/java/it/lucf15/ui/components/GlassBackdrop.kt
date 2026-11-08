package it.lucf15.ui.components

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.RenderEffect
import android.graphics.Shader
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.node.*
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import it.lucf15.ui.theme.ReaderTheme

@Stable
internal class GlassBackdrop(val layer: GraphicsLayer, var background: Color) {
    var source: LayoutCoordinates? = null
    val receivers = mutableSetOf<GlassNode>()
}

@Composable
internal fun rememberGlassBackdrop(): GlassBackdrop {
    val layer = rememberGraphicsLayer()
    val background = ReaderTheme.colors.background
    return remember(layer) { GlassBackdrop(layer, background) }.also { it.background = background }
}

internal fun Modifier.captureBackdrop(backdrop: GlassBackdrop): Modifier =
    this then GlassElement(backdrop, null)

@Composable
internal fun Modifier.glassBackdrop(backdrop: GlassBackdrop): Modifier {
    val filtered = rememberGraphicsLayer()
    val tint = ReaderTheme.colors.glassTint.toArgb()
    val radius = with(LocalDensity.current) { 12.5.dp.toPx() }
    val effect =
        remember(radius, tint) {
            val blur = RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP)
            RenderEffect.createColorFilterEffect(
                    BlendModeColorFilter(tint, BlendMode.SRC_OVER),
                    blur,
                )
                .asComposeRenderEffect()
        }
    SideEffect { filtered.renderEffect = effect }
    return this then GlassElement(backdrop, filtered)
}

private data class GlassElement(val backdrop: GlassBackdrop, val filtered: GraphicsLayer?) :
    ModifierNodeElement<GlassNode>() {
    override fun create() = GlassNode(backdrop, filtered)

    override fun update(node: GlassNode) {
        node.update(backdrop, filtered)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "glassBackdrop"
    }
}

internal class GlassNode(
    private var backdrop: GlassBackdrop,
    private var filtered: GraphicsLayer?,
) : Modifier.Node(), DrawModifierNode, LayoutAwareModifierNode {
    private var coordinates: LayoutCoordinates? = null

    override fun onAttach() {
        if (filtered != null) backdrop.receivers.add(this)
    }

    override fun onDetach() {
        if (filtered == null) backdrop.source = null else backdrop.receivers.remove(this)
        backdrop.receivers.forEach { if (it.isAttached) it.invalidateDraw() }
    }

    fun update(value: GlassBackdrop, layer: GraphicsLayer?) {
        if (isAttached && filtered != null) backdrop.receivers.remove(this)
        backdrop = value
        filtered = layer
        if (isAttached && filtered != null) backdrop.receivers.add(this)
        invalidateDraw()
    }

    override fun onPlaced(coordinates: LayoutCoordinates) {
        this.coordinates = coordinates
        if (filtered == null) backdrop.source = coordinates
        invalidateDraw()
    }

    override fun ContentDrawScope.draw() {
        val target = filtered
        if (target == null) {
            backdrop.layer.record {
                drawRect(backdrop.background)
                this@draw.drawContent()
            }
            drawLayer(backdrop.layer)
            backdrop.receivers.forEach { if (it.isAttached) it.invalidateDraw() }
        } else {
            val source = backdrop.source
            val receiver = coordinates
            if (source?.isAttached == true && receiver?.isAttached == true) {
                val offset = receiver.localPositionOf(source, Offset.Zero)
                target.record {
                    clipRect { translate(offset.x, offset.y) { drawLayer(backdrop.layer) } }
                }
                drawLayer(target)
            }
            drawContent()
        }
    }
}
