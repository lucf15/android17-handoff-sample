package it.lucf15.ui.scroll

import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.OverscrollFactory
import androidx.compose.ui.unit.Density

/** A separate effect/node per scroll container, matching JetBrains' iOS factory. */
data class CupertinoOverscrollFactory(private val density: Density) : OverscrollFactory {
    override fun createOverscrollEffect(): OverscrollEffect =
        CupertinoOverscrollEffect(density.density, applyClip = false)
}
