package com.matthewrussell.trchunkbrowser.domain

import com.matthewrussell.trchunkbrowser.model.AudioSegment
import javafx.collections.ObservableList
import java.util.regex.Pattern

fun ObservableList<AudioSegment>.sortedByLabel(): ObservableList<AudioSegment> {
    return this.sorted { o1, o2 ->
        val pattern = Pattern.compile("\\d+")
        val matcher = pattern.matcher(o1.label)
        val matcher2 = pattern.matcher(o2.label)

        if (matcher.find() && matcher2.find()) {
            val o1Num = matcher.group(0).toInt()
            val o2Num = matcher2.group(0).toInt()

            return@sorted o1Num - o2Num
        } else {
            return@sorted o1.label?.compareTo(o2.label)
        }
    }
}