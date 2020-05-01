package com.matthewrussell.trchunkbrowser.model

import com.matthewrussell.trwav.Metadata
import java.io.File
import java.util.regex.Pattern

data class AudioSegment(
    val src: File,
    val begin: Double,
    val end: Double,
    val label: String,
    val sourceMetadata: Metadata
): Comparable<AudioSegment> {
    override fun compareTo(other: AudioSegment): Int {
        // Sort Audio Segments by Label first then by Begin

        val labelComparator: Comparator<AudioSegment> = Comparator { o1, o2 ->
            val pattern = Pattern.compile("\\d+")
            val matcher = pattern.matcher(o1.label)
            val matcher2 = pattern.matcher(o2.label)

            var o1Match = "";
            while(matcher.find()) {
                o1Match += matcher.group()
            }

            var o2Match = "";
            while(matcher2.find()) {
                o2Match += matcher2.group()
            }

            if(o1Match != "" && o2Match != "") {
                val o1Num = o1Match.toLong()
                val o2Num = o2Match.toLong()

                // Sort by integer representation of the label
                return@Comparator (o1Num - o2Num).toInt()
            } else {
                // Sort normally
                return@Comparator o1.label.compareTo(o2.label)
            }
        }

        val beginComparator: Comparator<AudioSegment> = Comparator.comparing(AudioSegment::begin)

        return labelComparator.thenComparing(beginComparator).compare(this, other)
    }

}