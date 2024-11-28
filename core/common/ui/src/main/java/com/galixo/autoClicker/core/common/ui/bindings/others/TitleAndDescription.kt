package com.galixo.autoClicker.core.common.ui.bindings.others

import android.os.Build
import android.text.StaticLayout
import android.text.TextDirectionHeuristic
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import com.galixo.autoClicker.core.common.ui.bindings.setTextOrGone
import com.galixo.autoClicker.core.common.ui.databinding.IncludeTitleAndDescriptionBinding


 fun IncludeTitleAndDescriptionBinding.setTitle(titleText: String) {
    title.setTextOrGone(titleText)
}

 fun IncludeTitleAndDescriptionBinding.setupDescriptions(descriptions: List<String>) {
    if (descriptions.isEmpty()) {
        description.setTextOrGone(null)
        return
    }

    description.visibility = View.VISIBLE
    description.doOnLayout {
        var maxLinesCount = 0
        descriptions.forEach { descriptionText ->
            maxLinesCount = maxOf(maxLinesCount, description.getTextLineCount(descriptionText))
        }

        description.doOnPreDraw {
            description.setLines(maxLinesCount)
        }

        val oldState = description.tag as? DescriptionsState
        val descriptionIndex = oldState?.displayedIndex ?: 0
        if (descriptionIndex in descriptions.indices) description.text = descriptions[descriptionIndex]

        description.tag = oldState?.copy(descriptions = descriptions, maxLinesCount = maxLinesCount)
            ?: DescriptionsState(descriptions, null, maxLinesCount)
    }
}

 fun IncludeTitleAndDescriptionBinding.setDescription(text: String?) {
    description.setTextOrGone(text)
}

 fun IncludeTitleAndDescriptionBinding.setDescription(index: Int) {
    val state = description.tag as? DescriptionsState
    if (state == null) {
        description.tag = DescriptionsState(emptyList(), index, null)
        return
    }

    if (index !in state.descriptions.indices) return

    state.maxLinesCount?.let(description::setLines)
    description.text = state.descriptions[index]
    description.tag = state.copy(displayedIndex = index)
}

private fun TextView.getTextLineCount(textToShow: CharSequence): Int {
    if (width == 0 || layout == null) {
        Log.w(TAG, "Can't get text line count, layout width is 0")
        return 0
    }

    return getStaticLayout(textToShow).lineCount
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