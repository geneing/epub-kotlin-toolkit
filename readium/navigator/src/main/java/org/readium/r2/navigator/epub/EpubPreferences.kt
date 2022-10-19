/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.r2.navigator.epub

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.Serializable
import org.readium.r2.navigator.preferences.*
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.ReadingProgression
import org.readium.r2.shared.util.Language

@ExperimentalReadiumApi
@Serializable
data class EpubPreferences(
    val backgroundColor: Color? = null,
    val columnCount: ColumnCount? = null,
    val fontFamily: FontFamily? = null,
    val fontSize: Double? = null,
    val hyphens: Boolean? = null,
    val imageFilter: ImageFilter? = null,
    val language: Language? = null,
    val letterSpacing: Double? = null,
    val ligatures: Boolean? = null,
    val lineHeight: Double? = null,
    val pageMargins: Double? = null,
    val paragraphIndent: Double? = null,
    val paragraphSpacing: Double? = null,
    val publisherStyles: Boolean? = null,
    val readingProgression: ReadingProgression? = null,
    val scroll: Boolean? = null,
    val spread: Spread? = null,
    val textAlign: TextAlign? = null,
    val textColor: Color? = null,
    val textNormalization: TextNormalization? = null,
    val theme: Theme? = null,
    val typeScale: Double? = null,
    val verticalText: Boolean? = null,
    val wordSpacing: Double? = null
): Configurable.Preferences {

    operator fun plus(other: EpubPreferences): EpubPreferences =
        EpubPreferences(
            readingProgression = other.readingProgression ?: readingProgression,
            language = other.language ?: language,
            spread = other.spread ?: spread,
            backgroundColor = other.backgroundColor ?: backgroundColor,
            columnCount = other.columnCount ?: columnCount,
            fontFamily = other.fontFamily ?: fontFamily,
            fontSize = other.fontSize ?: fontSize,
            hyphens = other.hyphens ?: hyphens,
            imageFilter = other.imageFilter ?: imageFilter,
            letterSpacing = other.letterSpacing ?: letterSpacing,
            ligatures = other.ligatures ?: ligatures,
            lineHeight = other.lineHeight ?: lineHeight,
            pageMargins = other.pageMargins ?: pageMargins,
            paragraphIndent = other.paragraphIndent ?: paragraphIndent,
            paragraphSpacing = other.paragraphSpacing ?: paragraphSpacing,
            publisherStyles = other.publisherStyles ?: publisherStyles,
            scroll = other.scroll ?: scroll,
            textAlign = other.textAlign ?: textAlign,
            textColor = other.textColor ?: textColor,
            textNormalization = other.textNormalization ?: textNormalization,
            theme = other.theme ?: theme,
            typeScale = other.typeScale ?: typeScale,
            verticalText = other.verticalText ?: verticalText,
            wordSpacing = other.wordSpacing ?: wordSpacing
        )
}


/**
 * Loads the preferences from the legacy EPUB settings stored in the [SharedPreferences] with
 * given [sharedPreferencesName].
 *
 * This can be used to migrate the legacy settings to the new [EpubPreferences] format.
 *
 * If you changed the `fontFamilyValues` in the original Test App `UserSettings`, pass it to
 * [fontFamilies] to migrate the font family properly.
 */
@ExperimentalReadiumApi
fun EpubPreferences.Companion.fromLegacyEpubSettings(
    context: Context,
    sharedPreferencesName: String = "org.readium.r2.settings",
    fontFamilies: List<String> = listOf(
        "Original", "PT Serif", "Roboto", "Source Sans Pro", "Vollkorn", "OpenDyslexic",
        "AccessibleDfA", "IA Writer Duospace"
    )
): EpubPreferences {

    val sp: SharedPreferences =
        context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

    val fontFamily = sp
        .takeIf { it.contains("fontFamily") }
        ?.getInt("fontFamily", 0)
        ?.let { fontFamilies.getOrNull(it) }
        ?.takeUnless { it == "Original" }
        ?.let { FontFamily(it) }


    val theme = sp
        .takeIf { sp.contains("appearance") }
        ?.getInt("appearance", 0)
        ?.let {
            when (it) {
                0 -> Theme.LIGHT
                1 -> Theme.SEPIA
                2 -> Theme.DARK
                else -> null
            }
        }

    val scroll = sp
        .takeIf { sp.contains("scroll") }
        ?.getBoolean("scroll", false)

    val colCount = sp
        .takeIf { sp.contains("colCount") }
        ?.getInt("colCount", 0)
        ?.let {
            when (it) {
                0 -> ColumnCount.AUTO
                1 -> ColumnCount.ONE
                2 -> ColumnCount.TWO
                else -> null
            }
        }

    val pageMargins = sp
        .takeIf { sp.contains("pageMargins") }
        ?.getFloat("pageMargins", 1.0f)
        ?.toDouble()

    val fontSize = sp
        .takeIf { sp.contains("fontSize") }
        ?.let { sp.getFloat("fontSize", 0f) }
        ?.toDouble()
        ?.let { it / 100 }

    val textAlign = sp
        .takeIf { sp.contains("textAlign") }
        ?.getInt("textAlign", 0)
        ?.let {
            when (it) {
                0 -> TextAlign.JUSTIFY
                1 -> TextAlign.START
                else -> null
            }
        }

    val wordSpacing = sp
        .takeIf { sp.contains("wordSpacing") }
        ?.getFloat("wordSpacing", 0f)
        ?.toDouble()

    val letterSpacing = sp
        .takeIf { sp.contains("letterSpacing") }
        ?.getFloat("letterSpacing", 0f)
        ?.toDouble()
        ?.let { it * 2 }

    val lineHeight = sp
        .takeIf { sp.contains("lineHeight") }
        ?.getFloat("lineHeight", 1.2f)
        ?.toDouble()

    val publisherStyles = sp
        .takeIf { sp.contains("advancedSettings") }
        ?.getBoolean("advancedSettings", false)
        ?.let { !it }

    return EpubPreferences(
        fontFamily = fontFamily,
        theme = theme,
        scroll = scroll,
        columnCount = colCount,
        pageMargins = pageMargins,
        fontSize = fontSize,
        textAlign = textAlign,
        wordSpacing = wordSpacing,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
        publisherStyles = publisherStyles
    )
}
