package org.bibletranslationtools.trchunkbrowser.common.domain

import org.bibletranslationtools.trchunkbrowser.common.model.AudioSegment
import io.reactivex.Single
import java.io.File

class GetWavSegments(private val file: File) {
    fun segments(): Single<List<AudioSegment>> = GetBttrWavFile(file)
        .get()
        .map { wavFile ->
            ConvertWavFileToSegments(wavFile).segments()
        }
}
