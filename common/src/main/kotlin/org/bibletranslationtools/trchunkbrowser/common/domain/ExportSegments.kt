package org.bibletranslationtools.trchunkbrowser.common.domain

import io.reactivex.Completable
import io.reactivex.Single
import org.bibletranslationtools.trchunkbrowser.common.model.AudioSegment
import org.bibletranslationtools.trchunkbrowser.common.model.audio.BttrChunk
import org.bibletranslationtools.trchunkbrowser.common.model.audio.BttrMetadata
import org.bibletranslationtools.trchunkbrowser.common.model.audio.BttrWavFile
import org.wycliffeassociates.otter.common.audio.wav.*
import java.io.File

const val MODE_VERSE = "verse"
const val MODE_CHUNK = "chunk"

class ExportSegments(private val segments: List<AudioSegment>) {
    enum class MergeResult {
        SUCCESS,
        ERROR_DIFFERENT_BOOK_CHAPTER
    }

    private fun makeBttrFile(segment: AudioSegment): BttrWavFile {
        val bttrFile = segment.bttrFile

        // Sort markers
        bttrFile.metadata.markers = bttrFile.metadata.markers.sortedBy { it.location }.toMutableList()

        // Find the marker
        val markerIndex = bttrFile
            .metadata
            .markers
            .filter { it.label == segment.label }
            .map { bttrFile.metadata.markers.indexOf(it) }
            .first()

        val startAudioIndex = bttrFile.metadata.markers[markerIndex].location * DEFAULT_BITS_PER_SAMPLE / 8
        val endAudioIndex = if (markerIndex < bttrFile.metadata.markers.size - 1) {
            bttrFile.metadata.markers[markerIndex + 1].location * DEFAULT_BITS_PER_SAMPLE / 8
        } else {
            bttrFile.audio.size
        }
        val audioData = bttrFile.audio.copyOfRange(startAudioIndex, endAudioIndex)
        // Create the output wav file
        val marker = bttrFile.metadata.markers[markerIndex].copy(location = 0)
        val metadata = bttrFile.metadata.copy(markers = mutableListOf(marker))
        metadata.startv = segment.label
        metadata.endv = segment.label

        return BttrWavFile(bttrFile.src, bttrFile.wavFile, metadata, audioData)
    }

    private fun createWavFile(target: File, bttrWavFile: BttrWavFile) {
        val bttrChunk = BttrChunk()
        bttrChunk.metadata = bttrWavFile.metadata

        val cueChunk = CueChunk()
        bttrWavFile.metadata.markers.map {
            cueChunk.addCue(it)
        }

        val wavFile = WavFile(
            target,
            bttrWavFile.wavFile.channels,
            bttrWavFile.wavFile.sampleRate,
            bttrWavFile.wavFile.bitsPerSample,
            WavMetadata(listOf(bttrChunk, cueChunk))
        )

        WavOutputStream(wavFile).use {
            it.write(bttrWavFile.audio)
        }
    }

    fun exportSeparate(outputDir: File): Completable {
        return Completable.fromAction {
            for (segment in segments) {
                val newWav = makeBttrFile(segment)
                newWav.metadata.mode = MODE_VERSE
                val filename = generateFileName(segment.bttrFile.src, newWav.metadata)
                if (!outputDir.exists()) outputDir.mkdirs()
                val targetFile = outputDir.resolve(filename)

                createWavFile(targetFile, newWav)
            }
        }
    }

    fun exportMerged(outputDir: File, isChapter: Boolean = false): Single<MergeResult> {
        val books = segments.map { it.bttrFile.metadata.slug }.distinct()
        val chapters = segments.map { it.bttrFile.metadata.chapter }.distinct()
        if (books.size > 1 && chapters.size > 1) return Single.just(MergeResult.ERROR_DIFFERENT_BOOK_CHAPTER)
        return Single.fromCallable {
            val outputFiles = segments.map { makeBttrFile(it) }
            val metadata = outputFiles.first().metadata
            metadata.markers = outputFiles.map { Pair(it.metadata.markers, it.audio.size / (DEFAULT_BITS_PER_SAMPLE / 8)) }
                .reduce { acc, pair ->
                    pair.first.forEach { it.location += acc.second }
                    Pair(acc.first.plus(pair.first).toMutableList(), acc.second + pair.second)
                }.first
            metadata.mode = MODE_CHUNK
            metadata.endv = outputFiles.last().metadata.endv
            val audioData = outputFiles.map { it.audio }.reduce { acc, bytes -> acc.plus(bytes) }
            val filename = generateFileName(segments.first().bttrFile.src, metadata)
            val targetFileName = if (isChapter) {
                filename.replace(Regex("_v.*\\."), ".")
            } else filename
            val targetFile = outputDir.resolve(targetFileName)

            val bttrWavFile = BttrWavFile(
                targetFile,
                outputFiles.first().wavFile,
                metadata,
                audioData
            )

            createWavFile(targetFile, bttrWavFile)
            MergeResult.SUCCESS
        }
    }

    private fun generateFileName(sourceFile: File, newMetadata: BttrMetadata): String {
        val takeInfo = "t\\d+$".toRegex().find(sourceFile.nameWithoutExtension)?.value
        return newMetadata.toFilename(takeInfo ?: "t01")
    }
}
