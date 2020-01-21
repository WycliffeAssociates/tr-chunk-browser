package com.matthewrussell.trchunkbrowser.app.mainview

import com.github.thomasnield.rxkotlinfx.observeOnFx
import com.matthewrussell.trchunkbrowser.domain.ConvertDirectory
import com.matthewrussell.trchunkbrowser.domain.ExportSegments
import com.matthewrussell.trchunkbrowser.domain.GetWavSegments
import com.matthewrussell.trchunkbrowser.domain.sortedByLabel
import com.matthewrussell.trchunkbrowser.model.AudioSegment
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import tornadofx.*
import java.io.File

class MainViewModel : ViewModel() {
    val segments = FXCollections.observableArrayList<AudioSegment>()
    val hasSegments = SimpleListProperty<AudioSegment>().let {
        it.bind(SimpleObjectProperty(segments))
        it.emptyProperty().not()
    }
    val selectedSegments = FXCollections.observableArrayList<AudioSegment>()
    val selectedCount = selectedSegments.sizeProperty
    val snackBarMessages = PublishSubject.create<String>()
    val snackBarProgress = PublishSubject.create<Int>()
    val confirmConvertDirectory = PublishSubject.create<File>()

    fun importFile(file: File) {
        if (file.isDirectory) {
            confirmConvertDirectory.onNext(file)
//            convertDirectory(file)
            return
        }
        if (file.extension.toLowerCase() != "wav") {
            snackBarMessages.onNext(messages.getString("not_wav_file"))
            return
        }

        if (segments.map { it.src.name }.contains(file.name)) {
            snackBarMessages.onNext(messages.getString("file_already_imported"))
            return
        }

        GetWavSegments(file)
            .segments()
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .onErrorReturn {
                println(it)
                snackBarMessages.onNext(messages.getString("import_error"))
                listOf()
            }
            .doOnSuccess { retrieved ->
                segments.addAll(retrieved)
                segments.setAll(segments.sortedByLabel())
            }
            .subscribe()
    }

    fun convertDirectory(dir: File) {
        ConvertDirectory(dir)
            .convert()
            .doOnSubscribe {
                snackBarProgress.onNext(0)
            }
            .doOnComplete {
                snackBarProgress.onNext(100)
                snackBarMessages.onNext(messages.getString("done_exporting"))
            }
            .onErrorResumeNext {
                Completable.fromAction {
                    snackBarMessages.onNext(messages.getString("export_error"))
                }
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun split(outputDir: File) {
        ExportSegments(selectedSegments)
            .exportSeparate(outputDir)
            .observeOnFx()
            .subscribe {
                snackBarMessages.onNext(messages.getString("done_exporting"))
            }
        clearSelected()
    }

    fun merge(outputDir: File) {
        ExportSegments(selectedSegments)
            .exportMerged(outputDir)
            .observeOnFx()
            .subscribe { result ->
                if (result == ExportSegments.MergeResult.SUCCESS) {
                    snackBarMessages.onNext(messages.getString("done_exporting"))
                } else {
                    snackBarMessages.onNext(messages.getString("export_error"))
                }
            }
        clearSelected()
    }

    fun deleteSelected() {
        segments.removeAll(selectedSegments)
        clearSelected()
    }

    fun select(segment: AudioSegment) {
        if (!selectedSegments.contains(segment)) selectedSegments.add(segment)
        selectedSegments.sortedByLabel()
    }

    fun deselect(segment: AudioSegment) {
        selectedSegments.remove(segment)
    }

    fun selectAll() {
        selectedSegments.clear()
        selectedSegments.addAll(segments)
    }

    fun clearSelected() {
        selectedSegments.clear()
    }

    fun reset() {
        segments.clear()
    }
}