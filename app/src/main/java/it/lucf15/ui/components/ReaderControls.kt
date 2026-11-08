package it.lucf15.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import it.lucf15.ui.theme.*

@Composable
internal fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalReaderInk.current,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Clip,
    style: TextStyle = ReaderTheme.typography.bodyMedium,
) {
    BasicText(
        text,
        modifier,
        style.merge(
            TextStyle(
                color = color,
                fontSize = fontSize,
                fontWeight = fontWeight,
                lineHeight = lineHeight,
                letterSpacing = letterSpacing,
            ),
        ),
        overflow = overflow,
        maxLines = maxLines,
        minLines = minLines,
    )
}

@Composable
internal fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalReaderInk.current,
) {
    Image(
        imageVector,
        contentDescription,
        modifier.size(24.dp),
        colorFilter = ColorFilter.tint(tint),
    )
}

@Composable
internal fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier.size(48.dp).clip(CircleShape).clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@Composable
internal fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = ReaderTheme.colors
    Row(
        modifier
            .heightIn(min = 48.dp)
            .clip(CircleShape)
            .background(if (primary) colors.primary else colors.onSurface.copy(alpha = .06f))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(
            LocalReaderInk provides if (primary) colors.onPrimary else colors.onSurface,
        ) {
            content()
        }
    }
}

@Composable
internal fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ReaderTheme.colors
    BasicTextField(
        value,
        onValueChange,
        modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .semantics { contentDescription = "Search stories" },
        textStyle = ReaderTheme.typography.bodyMedium.copy(color = colors.onSurface),
        singleLine = true,
        cursorBrush = SolidColor(colors.primary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        decorationBox = { input ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) Text("Search stories", color = colors.onSurfaceVariant)
                input()
            }
        },
    )
}
