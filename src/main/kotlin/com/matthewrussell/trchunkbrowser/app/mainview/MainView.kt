package com.matthewrussell.trchunkbrowser.app.mainview

import com.github.thomasnield.rxkotlinfx.toObservable
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXCheckBox
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXSnackbar
import com.matthewrussell.trchunkbrowser.app.controls.materialdialog.MaterialDialogContent
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.event.EventHandler
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import tornadofx.*
import kotlin.math.floor

class MainView : View() {
    private val viewModel: MainViewModel by inject()

    init {
        importStylesheet<MainViewStyles>()
        title = messages["app_name"]
    }

    override val root = stackpane {
        vbox {
            addClass(MainViewStyles.root)
            add(JFXSnackbar(this).apply { // Progress snackbar
                viewModel.snackBarProgress.subscribe { progress ->
                    if(progress < 100) {
                        enqueue(JFXSnackbar.SnackbarEvent(
                            messages["export_in_progress"],
                            "",
                            0,
                            true,
                            null))
                    } else {
                        this.visibleProperty()
                            .toObservable()
                            .takeUntil { it }
                            .doOnComplete {
                                this.close()
                            }
                            .subscribe()
                    }
                }
            })
            add(JFXSnackbar(this).apply { // Messages snackbar
                viewModel.snackBarMessages.subscribe {
                    enqueue(JFXSnackbar.SnackbarEvent(it, "OK", 0, true, EventHandler {
                        this.close()
                    }))
                }
            })
            viewModel.confirmConvertDirectory.subscribe {
                val dialog = JFXDialog().apply {
                    dialogContainer = this@stackpane
                    isOverlayClose = false
                    content = MaterialDialogContent().apply {
                        title = messages["convert_folder_title"]
                        message = messages["convert_folder_message"]
                        confirmButtonText = messages["split"].toUpperCase()
                        cancelButtonText = messages["cancel"].toUpperCase()
                        confirmButton.action {
                            viewModel.convertDirectory(it)
                            close()
                        }
                        cancelButton.action {
                            close()
                        }
                    }
                }
                dialog.show()
            }
            hbox {
                addClass(MainViewStyles.actionBar)
                hbox {
                    hgrow = Priority.ALWAYS
                    addClass(MainViewStyles.barContent)
                    label(messages["app_name"]) {
                        visibleProperty().bind(viewModel.selectedCount.eq(0))
                        managedProperty().bind(visibleProperty())
                    }
                    add(JFXButton().apply {
                        visibleProperty().bind(viewModel.selectedCount.gt(0))
                        managedProperty().bind(visibleProperty())
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.CLOSE, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        action {
                            viewModel.clearSelected()
                        }
                    })
                    label(viewModel.selectedCount.asString()) {
                        visibleProperty().bind(viewModel.selectedCount.gt(0))
                        managedProperty().bind(visibleProperty())
                    }
                    spacer()
                    add(JFXButton().apply {
                        visibleProperty().bind(viewModel.selectedCount.eq(1))
                        managedProperty().bind(visibleProperty())
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.SHARE, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        text = messages["export"].toUpperCase()
                        action {
                            chooseDirectory(messages["choose_output_folder"])?.let {
                                viewModel.split(it)
                            }
                        }
                    })
                    add(JFXButton().apply {
                        visibleProperty().bind(viewModel.selectedCount.gt(1))
                        managedProperty().bind(visibleProperty())
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.CALL_SPLIT, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        text = messages["split"].toUpperCase()
                        action {
                            chooseDirectory(messages["choose_output_folder"])?.let {
                                viewModel.split(it)
                            }
                        }
                    })
                    add(JFXButton().apply {
                        visibleProperty().bind(viewModel.selectedCount.gt(1))
                        managedProperty().bind(visibleProperty())
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.CALL_MERGE, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        text = messages["merge"].toUpperCase()
                        action {
                            chooseDirectory(messages["choose_output_folder"])?.let {
                                viewModel.merge(it)
                            }
                        }
                    })
                    add(JFXButton().apply {
                        visibleProperty().bind(viewModel.selectedCount.gt(0))
                        managedProperty().bind(visibleProperty())
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.DELETE, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        text = messages["remove"].toUpperCase()
                        action {
                            viewModel.deleteSelected()
                        }
                    })
                    add(JFXButton().apply {
                        visibleProperty().bind(viewModel.hasSegments.and(viewModel.selectedCount.eq(0)))
                        managedProperty().bind(visibleProperty())
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.ADD, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        text = messages["add"].toUpperCase()
                        isDisableVisualFocus = true
                        action {
                            chooseAndImportWav()
                        }
                    })
                    add(JFXButton().apply {
                        visibleProperty().bind(viewModel.hasSegments.and(viewModel.selectedCount.eq(0)))
                        managedProperty().bind(visibleProperty())
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.CLEAR_ALL, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        text = messages["clear_all"].toUpperCase()
                        isDisableVisualFocus = true
                        action {
                            viewModel.reset()
                        }
                    })
                    add(JFXButton().apply {
                        visibleProperty().bind(viewModel.hasSegments)
                        managedProperty().bind(visibleProperty())
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.SELECT_ALL, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        text = messages["select_all"].toUpperCase()
                        isDisableVisualFocus = true
                        action {
                            viewModel.selectAll()
                        }
                    })
                }
            }
            stackpane {
                vgrow = Priority.ALWAYS
                vbox {
                    visibleProperty().bind(viewModel.hasSegments.not())
                    addClass(MainViewStyles.bigDragTarget)
                    add(MaterialIconView(MaterialIcon.ADD, "2em"))
                    label(messages["drop_here"])
                    setOnMouseClicked {
                        chooseAndImportWav()
                    }
                    onDragOver = EventHandler {
                        if (it.gestureSource != this && it.dragboard.hasFiles()) {
                            it.acceptTransferModes(TransferMode.COPY)
                        }
                        it.consume()
                    }
                    onDragDropped = EventHandler {
                        var success = false
                        if (it.dragboard.hasFiles()) {
                            for (file in it.dragboard.files) {
                                viewModel.importFile(file)
                            }
                            success = true
                        }
                        it.isDropCompleted = success
                        it.consume()
                    }
                }
                listview(viewModel.segments) {
                    visibleProperty().bind(viewModel.hasSegments)
                    managedProperty().bind(visibleProperty())
                    addClass(MainViewStyles.segmentList)
                    cellCache { segment ->
                        hbox {
                            addClass(MainViewStyles.segmentListItem)
                            val checkbox = JFXCheckBox().apply {
                                selectedProperty().onChange {
                                    if (it) viewModel.select(segment) else viewModel.deselect(segment)
                                }
                            }
                            viewModel.selectedSegments.onChange {
                                checkbox.isSelected = it.list.contains(segment)
                            }
                            add(checkbox)
                            val duration = segment.end - segment.begin
                            val minutes = floor(duration / 60.0)
                            val seconds = duration - minutes * 60.0

                            label("${messages[segment.sourceMetadata.slug]} " +
                                    "${segment.sourceMetadata.chapter.padStart(2, '0')}:" +
                                    "${segment.label.padStart(2, '0')}") {
                                addClass(MainViewStyles.segmentTitle)
                            }
                            val takeNum = "t\\d+$".toRegex()
                                .find(segment.src.nameWithoutExtension)
                                ?.value?.substring(1)

                            if(takeNum !== null) {
                                label("Take $takeNum") {
                                    addClass(MainViewStyles.segmentInfo)
                                }
                            }
                            spacer()
                            label("%02.0f:%02.2f".format(minutes, seconds)) {
                                addClass(MainViewStyles.segmentTime)
                            }
                            onMousePressed = EventHandler { checkbox.fireEvent(it) }
                            onMouseReleased = EventHandler { checkbox.fireEvent(it) }
                        }
                    }
                    onDragOver = EventHandler {
                        if (it.gestureSource != this && it.dragboard.hasFiles()) {
                            it.acceptTransferModes(TransferMode.COPY)
                        }
                        it.consume()
                    }
                    onDragDropped = EventHandler {
                        var success = false
                        if (it.dragboard.hasFiles()) {
                            for (file in it.dragboard.files) {
                                viewModel.importFile(file)
                            }
                            success = true
                        }
                        it.isDropCompleted = success
                        it.consume()
                    }
                }
            }

        }
    }

    fun chooseAndImportWav() {
        val wavFiles = chooseFile(
            messages["choose_wav_file"],
            arrayOf(FileChooser.ExtensionFilter("WAV File", "*.wav")),
            FileChooserMode.Single
        )
        for (file in wavFiles) {
            viewModel.importFile(file)
        }
    }
}