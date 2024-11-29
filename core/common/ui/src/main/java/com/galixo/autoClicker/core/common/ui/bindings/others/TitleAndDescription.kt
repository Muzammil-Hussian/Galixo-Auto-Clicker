package com.galixo.autoClicker.core.common.ui.bindings.others

import android.os.Build
import android.text.StaticLayout
import android.text.TextDirectionHeuristic
import android.widget.TextView
import com.galixo.autoClicker.core.common.ui.bindings.setTextOrGone
import com.galixo.autoClicker.core.common.ui.databinding.IncludeTitleAndDescriptionBinding


fun IncludeTitleAndDescriptionBinding.setTitle(titleText: String) {
    title.setTextOrGone(titleText)
}

fun IncludeTitleAndDescriptionBinding.setTitle(titleText: Int) {
    title.setTextOrGone(titleText)
}


fun IncludeTitleAndDescriptionBinding.setDescription(text: String?) {
    description.setTextOrGone(text)
}

fun IncludeTitleAndDescriptionBinding.setDescription(text: Int) {
    description.setTextOrGone(text)
}

private fun TextView.getStaticLayout(textToShow: CharSequence): StaticLayout = StaticLayout.Builder
    .obtain(textToShow, 0, textToShow.length, layout.paint, getTextAreaWidth())
    .setAlignment(layout.alignment)
    .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
    .setIncludePad(includeFontPadding)
    .setBreakStrategy(breakStrategy)
    .setHyphenationFrequency(hyphenationFrequency)
    .apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) setJustificationMode(justificationMode)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) setUseLineSpacingFromFallbacks(isFallbackLineSpacing)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) setTextDirection((textDirectionHeuristic as TextDirectionHeuristic?)!!)
    }
    .build()


private fun TextView.getTextAreaWidth(): Int =
    width - getCompoundPaddingLeft() - getCompoundPaddingRight()


private data class DescriptionsState(
    val descriptions: List<String> = emptyList(),
    val displayedIndex: Int? = null,
    val maxLinesCount: Int? = null,
)

private const val TAG = "TitleAndDescription"