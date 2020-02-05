package com.matthewrussell.trchunkbrowser.domain

import com.matthewrussell.trchunkbrowser.model.AudioSegment
import com.matthewrussell.trwav.Metadata
import java.io.File

class ConvertMetadataToSegments(private val metadata: Metadata, private val src: File, private val duration: Double) {
    fun segments(): List<AudioSegment> {
        val segments: MutableList<AudioSegment> = mutableListOf()
        for (i in 0 until metadata.markers.size) {
            val segment = AudioSegment(
                src,
                metadata.markers[i].location / 44100.0,
                if (i < metadata.markers.size - 1) {
                    metadata.markers[i+1].location / 44100.0
                } else {
                    duration
                },
                metadata.markers[i].label,
                metadata
            )
            segments.add(segment)
        }
        return segments
    }
}