package org.bibletranslationtools.trchunkbrowser.common.domain

import io.reactivex.Completable
import java.io.File

class DirectorySplit(private val inputDir: File, private val outputDir: File? = null) {
    fun split(rootDir: File = inputDir.parentFile): Completable {
        val outDir = outputDir ?: rootDir.resolve("${inputDir.name}-out")
        return Completable
            .fromAction {
                splitDirectory(inputDir, outDir)
            }
            .doOnError {
                outDir.deleteRecursively()
            }
    }

    private fun splitDirectory(inputDir: File, outputDir: File) {
        inputDir.listFiles()?.let {
            for (file in it.toList()) {
                if (file.isDirectory) {
                    splitDirectory(file, File(outputDir, file.name))
                }
                else {
                    // Skip non-wav files and files without verse information
                    // Usually it's the chapter compilation files
                    val parts = file.nameWithoutExtension.split("_")
                    val verseWidth = parts.filter { it.startsWith("v") }
                    var shouldSkip = verseWidth.isEmpty()

                    // If there is only one file (compiled chapter) in directory
                    // then convert it
                    if(verseWidth.isEmpty()) {
                        val parentDir = file.parentFile
                        if (parentDir.listFiles().size == 1) {
                            shouldSkip = false
                        }
                    }

                    if((file.extension.toLowerCase() == "wav") && !shouldSkip) {
                        splitFile(file, outputDir)
                    }
                }
            }
        }
    }

    private fun splitFile(wav: File, outputDir: File) {
        ExportSegments(GetWavSegments(wav).segments().blockingGet())
            .exportSeparate(outputDir)
            .blockingAwait()
    }
}
