package com.matthewrussell.trchunkbrowser.domain

import io.reactivex.Completable
import java.io.File

class ConvertDirectory(private val inputDir: File) {
    fun convert(rootDir: File = inputDir.parentFile): Completable {
        val outputDir = rootDir.resolve("${inputDir.name}-out")
        return Completable
            .fromAction {
                convertDirectory(inputDir, outputDir)
            }
            .doOnError {
                outputDir.deleteRecursively()
            }
    }

    private fun convertDirectory(inputDir: File, outputDir: File) {
        inputDir.listFiles()?.let {
            for (file in it.toList()) {
                if (file.isDirectory) {
                    convertDirectory(file, File(outputDir, file.name))
                }
                else {
                    if(file.extension == "wav" || file.extension == "WAV") {
                        convertFile(file, outputDir)
                    }
                }
            }
        }
    }

    private fun convertFile(wav: File, outputDir: File) {
        ExportSegments(GetWavSegments(wav).segments().blockingGet())
            .exportSeparate(outputDir)
            .blockingAwait()
    }
}