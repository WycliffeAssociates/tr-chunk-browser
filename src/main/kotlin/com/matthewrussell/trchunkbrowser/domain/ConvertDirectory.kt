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
                    // Skip non-wav files and files without verse information
                    // Usually it's the chapter compilation files
                    val parts = file.nameWithoutExtension.split("_")
                    val verseWidth = parts.filter { it.startsWith("v") }

                    if((file.extension.toLowerCase() == "wav") && verseWidth.isNotEmpty()) {
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