package com.thecode.infotify.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * Palette derived from Infotify's existing orange, which is the brand equity users already
 * recognise — but desaturated for surfaces and reserved for accents, so the interface reads
 * as a reading app rather than a warning label.
 *
 * Neutrals carry a slight warm bias so they sit with the accent instead of fighting it.
 */

// Brand
internal val Ember = Color(0xFFD8500B)
internal val EmberBright = Color(0xFFFF8A3D)
internal val EmberContainerLight = Color(0xFFFFE1D0)
internal val EmberContainerDark = Color(0xFF5C2503)
internal val OnEmberContainerLight = Color(0xFF3A1500)
internal val OnEmberContainerDark = Color(0xFFFFDBC8)

// Secondary — a muted slate that reads as "editorial" next to the ember
internal val Slate = Color(0xFF5C5A57)
internal val SlateBright = Color(0xFFC8C4BF)
internal val SlateContainerLight = Color(0xFFE7E2DC)
internal val SlateContainerDark = Color(0xFF444240)

// Neutrals — warm-biased greys, never pure
internal val PaperLight = Color(0xFFFBF8F5)
internal val SurfaceLight = Color(0xFFFFFFFF)
internal val SurfaceVariantLight = Color(0xFFF1ECE6)
internal val OutlineLight = Color(0xFFD5CEC6)
internal val InkLight = Color(0xFF1C1A18)
internal val InkMutedLight = Color(0xFF55504B)

internal val PaperDark = Color(0xFF141312)
internal val SurfaceDark = Color(0xFF1D1B1A)
internal val SurfaceVariantDark = Color(0xFF2A2724)
internal val OutlineDark = Color(0xFF48433E)
internal val InkDark = Color(0xFFEDE8E3)
internal val InkMutedDark = Color(0xFFB5AEA7)

// Container tones.
//
// Material 3 falls back to its own purple-leaning defaults for every surfaceContainer role
// left undefined, which is why dialogs and menus were rendering lavender against a warm
// palette. Defining them keeps every elevated surface in the same family as the page.
internal val SurfaceDimLight = Color(0xFFE9E4DD)
internal val SurfaceBrightLight = Color(0xFFFFFFFF)
internal val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
internal val SurfaceContainerLowLight = Color(0xFFFAF7F3)
internal val SurfaceContainerLight = Color(0xFFF4F0EA)
internal val SurfaceContainerHighLight = Color(0xFFEEE9E2)
internal val SurfaceContainerHighestLight = Color(0xFFE8E2DA)
internal val InverseSurfaceLight = Color(0xFF302D29)
internal val InverseOnSurfaceLight = Color(0xFFF4F0EA)

internal val SurfaceDimDark = Color(0xFF121110)
internal val SurfaceBrightDark = Color(0xFF38352F)
internal val SurfaceContainerLowestDark = Color(0xFF0D0C0B)
internal val SurfaceContainerLowDark = Color(0xFF1A1918)
internal val SurfaceContainerDark = Color(0xFF1E1D1B)
internal val SurfaceContainerHighDark = Color(0xFF292724)
internal val SurfaceContainerHighestDark = Color(0xFF34312D)
internal val InverseSurfaceDark = Color(0xFFE7E2DA)
internal val InverseOnSurfaceDark = Color(0xFF1E1D1B)

// Semantic — separate from the accent on purpose
internal val ErrorLight = Color(0xFFB3261E)
internal val ErrorDark = Color(0xFFF2837B)
internal val ErrorContainerLight = Color(0xFFF9DEDC)
internal val ErrorContainerDark = Color(0xFF561E19)
