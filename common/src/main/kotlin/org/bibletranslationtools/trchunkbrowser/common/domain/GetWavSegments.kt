package org.bibletranslationtools.trchunkbrowser.common.domain

import org.bibletranslationtools.trchunkbrowser.common.model.AudioSegment
import io.reactivex.Single
import java.io.File

class GetWavSegments(private val wav: File) {
    fun segments(): Single<List<AudioSegment>> = GetWavMetadata(wav)
        .metadata()
        .flatMap { metadata ->
            GetWavDuration(wav).duration().map { duration ->
                ConvertMetadataToSegments(metadata, wav, duration).segments()
            }
        }
}
