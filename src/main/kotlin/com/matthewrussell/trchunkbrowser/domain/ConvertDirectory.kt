package com.matthewrussell.trchunkbrowser.domain

import io.reactivex.Completable
import java.io.File

class ConvertDirectory(private val inputDir: File) {
    fun convert(rootDir: File = inputDir.parentFile): Completable {
        val outputDir = rootDir.resolve("${inputDir.name}-out")
        return Completable
            .fromAction {
                export(inputDir, outputDir)
            }
            .doOnError {
                outputDir.deleteRecursively()
            }
    }

    private fun export(inputDir: File, outputDir: File) {
        val contents = inputDir.listFiles().toList()
        val wavFiles = contents.filter {
            it.isFile && (it.extension == "wav" || it.extension == "WAV")
        }
        for (wav in wavFiles) {
            ExportSegments(GetWavSegments(wav).segments().blockingGet())
                .exportSeparate(outputDir)
                .blockingAwait()
        }
        val subDirs = contents.filter { it.isDirectory }
        for (sub in subDirs) {
            export(sub, File(outputDir, sub.name))
        }
    }
}