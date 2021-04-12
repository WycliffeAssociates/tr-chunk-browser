package org.bibletranslationtools.trchunkbrowser.common.domain

import io.reactivex.Completable
import org.bibletranslationtools.trchunkbrowser.common.model.AudioSegment
import java.io.File

class DirectoryMerge {
    fun merge(inputDir: File, outputDir: File? = null): Completable {
        val mergeOutput = outputDir
            ?: inputDir.parentFile.resolve("${inputDir.name}-out").apply { mkdirs() }

        return Completable.fromCallable {
            mergeSubDirRecursively(inputDir, mergeOutput)
        }
    }

    private fun mergeSubDirRecursively(root: File, outputDir: File) {
        val segments = mutableListOf<AudioSegment>()
        root.walk().filter { it.isDirectory }.forEach { dir ->
            val filesInDir = dir.listFiles(File::isFile)
            if (filesInDir.any()) {
                segments.clear()
                filesInDir.forEach {
                    segments.addAll(GetWavSegments(it).segments().blockingGet())
                }
                ExportSegments(segments)
                    .exportMerged(outputDir, true)
                    .subscribe()
            }
        }
    }
}
