package com.matthewrussell.trchunkbrowser.domain

import com.matthewrussell.trchunkbrowser.model.AudioSegment
import com.matthewrussell.trwav.BITS_PER_SAMPLE
import com.matthewrussell.trwav.Metadata
import com.matthewrussell.trwav.WavFile
import com.matthewrussell.trwav.WavFileReader
import com.matthewrussell.trwav.WavFileWriter
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

const val MODE_VERSE = "verse"
const val MODE_CHUNK = "chunk"

class ExportSegments(private val segments: List<AudioSegment>) {
    enum class MergeResult {
        SUCCESS,
        ERROR_DIFFERENT_BOOK_CHAPTER
    }

    private val cache = hashMapOf<File, WavFile>()

    private fun makeWavFile(segment: AudioSegment): WavFile {
        // Get the source wav file
        val source = segment.src
        val file = if (cache.containsKey(source)) {
            cache[source]!!
        } else {
            cache[source] = WavFileReader().read(source)
            cache[source]!!
        }

        // Find the marker
        val markerIndex = file
            .metadata
            .markers
            .filter { it.label == segment.label }
            .map { file.metadata.markers.indexOf(it) }
            .first()

        val startAudioIndex = file.metadata.markers[markerIndex].position * BITS_PER_SAMPLE / 8
        val endAudioIndex = if (markerIndex < file.metadata.markers.size - 1) {
            file.metadata.markers[markerIndex + 1].position * BITS_PER_SAMPLE / 8
        } else {
            file.audio.size - 1
        }
        val audioData =  file.audio.copyOfRange(startAudioIndex, endAudioIndex)

        // Create the output wav file
        val marker = file.metadata.markers[markerIndex].copy(position = 0)
        val metadata = file.metadata.copy(markers = mutableListOf(marker))
        metadata.startv = segment.label
        metadata.endv = segment.label
        return  WavFile(metadata, audioData)
    }

    fun exportSeparate(outputDir: File): Completable {
        return Completable.fromAction {
            for (segment in segments) {
                val newWav = makeWavFile(segment)
                newWav.metadata.mode = MODE_VERSE
                val filename = generateFileName(segment.src, newWav.metadata)
                if (!outputDir.exists()) outputDir.mkdirs()
                WavFileWriter().write(newWav, outputDir.resolve(filename))
            }
        }
    }

    fun exportMerged(outputDir: File): Single<MergeResult> {
        val books = segments.map { it.sourceMetadata.slug }.distinct()
        val chapters = segments.map { it.sourceMetadata.chapter }.distinct()
        if (books.size > 1 && chapters.size > 1) return Single.just(MergeResult.ERROR_DIFFERENT_BOOK_CHAPTER)
        return Single.fromCallable {
            val outputFiles = segments.map { makeWavFile(it) }
            val metadata = outputFiles.first().metadata
            metadata.markers = outputFiles.map { Pair(it.metadata.markers, it.audio.size / (BITS_PER_SAMPLE / 8)) }.reduce { acc, pair ->
                pair.first.forEach { it.position += acc.second }
                Pair(acc.first.plus(pair.first).toMutableList(), acc.second + pair.second)
            }.first
            metadata.mode = MODE_CHUNK
            metadata.endv = outputFiles.last().metadata.endv
            val audioData = outputFiles.map { it.audio }.reduce { acc, bytes -> acc.plus(bytes) }
            val filename = generateFileName(segments.first().src, metadata)
            val wavFile = WavFile(metadata, audioData)
            WavFileWriter().write(wavFile, outputDir.resolve(filename))
            MergeResult.SUCCESS
        }
    }

    private fun generateFileName(sourceFile: File, newMetadata: Metadata): String {
        val parts = sourceFile.nameWithoutExtension.split("_")
        val verseWidth = parts.filter { it.startsWith("v") }.last().split("-").first().length - 1
        val chapterWidth = parts.filter { it.startsWith("c") }.last().length - 1
        return newMetadata.toFilename(parts.last(), chapterWidth, verseWidth)
    }
}