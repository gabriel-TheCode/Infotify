package com.thecode.infotify.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.thecode.infotify.designsystem.theme.InfotifyTheme
import com.thecode.infotify.designsystem.theme.WordmarkFamily

/**
 * The Infotify wordmark: "Infotify" with the dot of the "i" as the brand's only piece of
 * proprietary motion.
 *
 * The dot falls in and settles once, on a cold start. It is deliberately the single place
 * this gesture appears — a sign repeated everywhere stops being a sign. When [animated] is
 * false, or when the system has animations disabled, the final state renders directly.
 */
@Composable
fun Wordmark(
    modifier: Modifier = Modifier,
    fontSize: Int = 22,
    animated: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onBackground,
    dotColor: Color = MaterialTheme.colorScheme.primary
) {
    val density = LocalDensity.current
    val isPreview = LocalInspectionMode.current

    // Travel is proportional to the type size, so the gesture reads the same at 22sp in a
    // top bar and at 44sp on the welcome screen.
    val travelPx = with(density) { (fontSize * 1.6f).dp.toPx() }

    val drop = remember { Animatable(if (animated && !isPreview) -1f else 0f) }
    val fade = remember { Animatable(if (animated && !isPreview) 0f else 1f) }

    LaunchedEffect(animated) {
        if (!animated || isPreview) return@LaunchedEffect
        fade.animateTo(1f, tween(durationMillis = 180, easing = LinearOutSlowInEasing))
        // Slight overshoot, then settle: a dot that lands dead-still reads as a static
        // image, one that bounces forever reads as a gadget.
        drop.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = 0.52f,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    val text = buildAnnotatedString {
        append("Infot")
        // The dotless "ı" leaves room for the dot drawn separately below.
        withStyle(SpanStyle(color = color)) { append("ı") }
        append("fy")
    }

    Box(modifier = modifier.clearAndSetSemantics { contentDescription = "Infotify" }) {
        Text(
            text = text,
            fontFamily = WordmarkFamily,
            fontSize = fontSize.sp,
            letterSpacing = (-fontSize * 0.03f).sp,
            color = color,
            modifier = Modifier.drawWithContent {
                drawContent()
                translate(top = drop.value * travelPx) {
                    drawCircle(
                        color = dotColor,
                        radius = size.height * DOT_RADIUS_RATIO,
                        center = Offset(
                            x = size.width * DOT_CENTER_X_RATIO,
                            y = size.height * DOT_CENTER_Y_RATIO
                        ),
                        alpha = fade.value
                    )
                }
            }
        )
    }
}

/**
 * Placement of the dot over the dotless i, expressed as fractions of the rendered text box
 * so it tracks the type size instead of being pinned to one.
 */
private const val DOT_RADIUS_RATIO = 0.058f
private const val DOT_CENTER_X_RATIO = 0.665f
private const val DOT_CENTER_Y_RATIO = 0.235f

@Preview(showBackground = true)
@Composable
private fun WordmarkPreview() = InfotifyTheme {
    Wordmark(fontSize = 40, modifier = Modifier.padding(24.dp))
}
