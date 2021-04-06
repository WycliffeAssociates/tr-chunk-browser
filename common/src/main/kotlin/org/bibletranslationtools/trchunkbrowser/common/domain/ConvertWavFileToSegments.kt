package org.bibletranslationtools.trchunkbrowser.common.domain

import org.bibletranslationtools.trchunkbrowser.common.model.AudioSegment
import org.bibletranslationtools.trchunkbrowser.common.model.audio.BttrWavFile
import org.bibletranslationtools.trchunkbrowser.common.model.book.BookLoader
import org.wycliffeassociates.otter.common.audio.wav.DEFAULT_BITS_PER_SAMPLE
import org.wycliffeassociates.otter.common.audio.wav.DEFAULT_SAMPLE_RATE
import org.wycliffeassociates.otter.common.audio.wav.WavCue

class ConvertWavFileToSegments(
    private val bttrFile: BttrWavFile
) {
    private val wavFile = bttrFile.wavFile
    private val cues = wavFile.metadata.getCues()
    private val totalDuration = (wavFile.totalAudioLength / (DEFAULT_BITS_PER_SAMPLE / 8) / DEFAULT_SAMPLE_RATE)
        .toDouble()

    fun segments(): List<AudioSegment> {
        tryFixMetadata()
        matchCuePoints()

        return cues
            .mapIndexed { i, cue ->
                val begin = (cue.location / DEFAULT_SAMPLE_RATE).toDouble()
                val end = try {
                    (cues[i+1].location / DEFAULT_SAMPLE_RATE).toDouble()
                } catch (e: Exception) {
                    totalDuration
                }
                AudioSegment(bttrFile, begin, end, cue.label)
            }
    }

    private fun tryFixMetadata() {
        val books = BookLoader.getBooks()
        val filenameMetadata = bttrFile.metadata.fromFilename(bttrFile.src.nameWithoutExtension, books)

        if(bttrFile.metadata.language == "") {
            bttrFile.metadata.language = filenameMetadata.language
        }
        if(bttrFile.metadata.anthology == "") {
            bttrFile.metadata.anthology = filenameMetadata.anthology
        }
        if(bttrFile.metadata.version == "") {
            bttrFile.metadata.version = filenameMetadata.version
        }
        if(bttrFile.metadata.bookNumber == "") {
            bttrFile.metadata.bookNumber = filenameMetadata.bookNumber
        }
        if(bttrFile.metadata.slug == "") {
            bttrFile.metadata.slug = filenameMetadata.slug
        }
        if(bttrFile.metadata.chapter == "") {
            bttrFile.metadata.chapter = filenameMetadata.chapter
        }
        if(bttrFile.metadata.startv == "") {
            bttrFile.metadata.startv = filenameMetadata.startv
        }
        if(bttrFile.metadata.endv == "") {
            bttrFile.metadata.endv = filenameMetadata.endv
        }
    }

    private fun matchCuePoints() {
        // Match labels and cue point ids,
        // and also match metadata point positions with cue point positions
        bttrFile.metadata.markers.forEach { cue ->
            val matchedCues = cues.filter { it.label == cue.label }
            if (matchedCues.isNotEmpty()) {
                cue.label = matchedCues.first().label
                cue.location = matchedCues.first().location
            }
        }

        // Create absent metadata markers from cue points
        cues.forEach { cue ->
            if (bttrFile.metadata.markers.contains(cue).not()) {
                bttrFile.metadata.markers.add(WavCue(cue.location, cue.label))
            }
        }
    }
}
