package org.bibletranslationtools.trchunkbrowser.common.domain

import com.matthewrussell.trwav.WavFileReader
import io.reactivex.Single
import java.io.File

class GetWavDuration(private val wavFile: File) {
    fun duration(): Single<Double> = Single
        .fromCallable {
            WavFileReader(wavFile).duration()
        }
}
