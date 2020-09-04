package org.bibletranslationtools.trchunkbrowser.cli

import org.bibletranslationtools.trchunkbrowser.common.domain.DirectorySplit
import org.bibletranslationtools.trchunkbrowser.common.domain.ExportSegments
import org.bibletranslationtools.trchunkbrowser.common.domain.GetWavSegments
import org.bibletranslationtools.trchunkbrowser.common.model.AudioSegment
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

class CommandLineController {
    val logger = Logger.getLogger(javaClass.name)
    private val segments = mutableListOf<AudioSegment>()

    fun importFile(file: File): Single<List<AudioSegment>> {
        return GetWavSegments(file)
            .segments()
    }

    fun importFiles(files: List<File>) {
        files.toObservable()
            .concatMap {
                importFile(it).toObservable()
            }
            .blockingSubscribe {
                segments.addAll(it)
                segments.sort()
            }
    }

    fun splitDirectory(inputDir: File, outputDir: File? = null) {
        DirectorySplit(inputDir, outputDir)
            .split()
            .doOnComplete {
                logger.log(Level.INFO, "Export complete")
            }
            .onErrorResumeNext {
                Completable.fromAction {
                    logger.log(Level.WARNING, "Unable to finish export. Fix incorrect metadata and try again")
                }
            }
            .subscribe()
    }

    fun split(outputDir: File) {
        ExportSegments(segments)
            .exportSeparate(outputDir)
            .subscribe {
                logger.log(Level.INFO, "Export complete")
            }
    }

    fun merge(outputDir: File) {
        ExportSegments(segments)
            .exportMerged(outputDir)
            .subscribe { result ->
                if (result == ExportSegments.MergeResult.SUCCESS) {
                    logger.log(Level.INFO, "Export complete")
                } else {
                    logger.log(Level.WARNING, "Unable to finish export. Fix incorrect metadata and try again")
                }
            }
    }
}
