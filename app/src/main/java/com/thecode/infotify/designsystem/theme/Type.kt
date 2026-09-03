package com.thecode.infotify.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.thecode.infotify.R

/**
 * Two contemporary grotesques, no serif.
 *
 * [Display] — Bricolage Grotesque, for the wordmark and headlines. Its width axis is the
 * reason it is here: French headlines run 15–20% longer than their English equivalents, so
 * headline styles narrow the face slightly to fit three lines instead of four, while the
 * wordmark narrows further still. One file, several behaviours.
 *
 * [Text] — Schibsted Grotesk, drawn by Bakken & Bæck for the Schibsted media group and
 * built for editorial work. It takes over wherever size drops: sources, timestamps, body
 * copy, every interface label.
 *
 * Both are SIL Open Font Licence and bundled, so the app renders its own identity on the
 * first frame rather than falling back to the system face while a download completes. It
 * also makes the previous San Francisco licensing problem structurally impossible.
 */

/** Optical size, weight and width, in the order Bricolage declares its axes. */
@OptIn(ExperimentalTextApi::class)
private fun bricolage(weight: Int, width: Int, opticalSize: Int) = Font(
    resId = R.font.bricolage_grotesque,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(
        FontVariation.Setting("opsz", opticalSize.toFloat()),
        FontVariation.Setting("wght", weight.toFloat()),
        FontVariation.Setting("wdth", width.toFloat())
    )
)

@OptIn(ExperimentalTextApi::class)
private fun schibsted(weight: Int) = Font(
    resId = R.font.schibsted_grotesk,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight))
)

/** Narrow and heavy: reserved for the wordmark, where compactness is the point. */
val WordmarkFamily = FontFamily(bricolage(weight = 800, width = 88, opticalSize = 96))

/** Headlines. Slightly narrowed so long French titles keep to three lines. */
val DisplayFamily = FontFamily(
    bricolage(weight = 800, width = 92, opticalSize = 72),
    bricolage(weight = 700, width = 94, opticalSize = 36),
    bricolage(weight = 600, width = 96, opticalSize = 24)
)

/** Everything below headline size. */
val TextFamily = FontFamily(
    schibsted(400),
    schibsted(500),
    schibsted(600),
    schibsted(700)
)

val InfotifyTypography = Typography(
    // Featured story
    displaySmall = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 30.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.6).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.4).sp
    ),
    // Screen titles
    headlineSmall = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 25.sp,
        letterSpacing = (-0.3).sp
    ),
    // List card headlines
    titleMedium = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.2).sp
    ),
    titleSmall = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 19.sp
    ),
    // Standfirst and running text
    bodyLarge = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    // Source name, timestamp, chips, buttons
    labelLarge = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 16.sp
    ),
    labelMedium = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.1.sp
    ),
    // Section eyebrows
    labelSmall = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.9.sp
    )
)
