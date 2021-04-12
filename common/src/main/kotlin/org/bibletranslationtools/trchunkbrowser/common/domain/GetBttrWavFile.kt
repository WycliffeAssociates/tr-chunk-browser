package org.bibletranslationtools.trchunkbrowser.common.domain

import io.reactivex.Single
import org.bibletranslationtools.trchunkbrowser.common.model.audio.BttrChunk
import org.bibletranslationtools.trchunkbrowser.common.model.audio.BttrWavFile
import org.wycliffeassociates.otter.common.audio.wav.WavFile
import org.wycliffeassociates.otter.common.audio.wav.WavMetadata
import java.io.File
import java.io.RandomAccessFile

class GetBttrWavFile(private val file: File) {
    fun get(): Single<BttrWavFile> = Single
        .fromCallable {
            val bttrChunk = BttrChunk()
            val wavMetadata = WavMetadata(listOf(bttrChunk))
            val wavFile = WavFile(file, wavMetadata)
            val audioData = ByteArray(wavFile.totalAudioLength)
            RandomAccessFile(file, "r").use {
                it.read(audioData)
            }

            BttrWavFile(file, wavFile, bttrChunk.metadata, audioData)
        }
}
