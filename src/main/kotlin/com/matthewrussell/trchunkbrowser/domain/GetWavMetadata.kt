package com.matthewrussell.trchunkbrowser.domain

import com.matthewrussell.trwav.Metadata
import com.matthewrussell.trwav.WavFileReader
import io.reactivex.Single
import java.io.File

class GetWavMetadata(private val wavFile: File) {
    fun metadata(): Single<Metadata> = Single
        .fromCallable {
            WavFileReader(wavFile).readMetadata()
        }
}