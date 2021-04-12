package org.bibletranslationtools.trchunkbrowser.common.model.audio

import org.wycliffeassociates.otter.common.audio.wav.WavFile
import java.io.File

class BttrWavFile(
    val src: File,
    val wavFile: WavFile,
    val metadata: BttrMetadata,
    val audio: ByteArray
)
