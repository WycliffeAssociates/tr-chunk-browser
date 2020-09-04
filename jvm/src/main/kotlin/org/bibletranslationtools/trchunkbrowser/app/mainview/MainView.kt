package org.bibletranslationtools.trchunkbrowser.app.mainview

import com.github.thomasnield.rxkotlinfx.toObservable
import com.jfoenix.controls.*
import org.bibletranslationtools.trchunkbrowser.app.controls.materialdialog.MaterialDialogContent
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.event.EventHandler
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import javafx.util.Duration
import org.bibletranslationtools.trchunkbrowser.common.domain.Properties
import tornadofx.*
import java.util.*
import kotlin.math.floor

class MainView : View() {
    private val viewModel: MainViewModel by inject()

    init {
        FX.locale = Locale(Properties.config.getString("lang", "en"))

        importStylesheet<MainViewStyles>()
        title = messages.getString("app_name")
    }

    override val root = stackpane {
        vbox {
            addClass(MainViewStyles.root)
            add(JFXSnackbar(this).apply { // Progress snackbar
                viewModel.snackBarProgress.subscribe { progress ->
                    if(progress < 100) {
                        enqueue(JFXSnackbar.SnackbarEvent(
                            JFXSnackbarLayout(messages.getString("export_in_progress")),
                            Duration.INDEFINITE,
                            null
                        ))
                    } else {
                        this.visibleProperty()
                            .toObservable()
                            .takeUntil { it }
                            .doOnComplete { this.close() }
                            .subscribe()
                    }
                }
            })
            add(JFXSnackbar(this).apply { // Messages snackbar
                viewModel.snackBarMessages.subscribe { message ->
                    enqueue(JFXSnackbar.SnackbarEvent(
                        JFXSnackbarLayout(
                            message,
                            messages.getString("ok"),
                            EventHandler { this.close() }
                        ),
                        Duration.INDEFINITE,
                        null
                    ))
                }
            })
            viewModel.confirmConvertDirectory.subscribe {
                val dialog = JFXDialog().apply {
                    dialogContainer = this@stackpane
                    isOverlayClose = false
                    content = MaterialDialogContent().apply {
                        title = messages.getString("convert_folder_title")
                        message = messages.getString("convert_folder_message")
                        confirmButtonText = messages.getString("split").toUpperCase()
                        cancelButtonText = messages.getString("cancel").toUpperCase()
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
                    label {
                        text = messages.getString("app_name")
                        visibleWhen(viewModel.selectedCount.eq(0))
                        managedWhen(visibleProperty())
                    }
                    add(JFXButton().apply {
                        visibleWhen(viewModel.selectedCount.gt(0))
                        managedWhen(visibleProperty())
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.CLOSE, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        action {
                            viewModel.clearSelected()
                        }
                    })
                    label(viewModel.selectedCount.asString()) {
                        visibleWhen(viewModel.selectedCount.gt(0))
                        managedWhen(visibleProperty())
                    }
                    spacer()
                    add(JFXButton().apply {
                        visibleWhen(viewModel.selectedCount.eq(1))
                        managedWhen(visibleProperty())
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.SHARE, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        text = messages.getString("export").toUpperCase()
                        action {
                            chooseDirectory(messages.getString("choose_output_folder"))?.let {
                                viewModel.split(it)
                            }
                        }
                    })
                    add(JFXButton().apply {
                        visibleWhen(viewModel.selectedCount.gt(1))
                        managedWhen(visibleProperty())
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.CALL_SPLIT, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        text = messages.getString("split").toUpperCase()
                        action {
                            chooseDirectory(messages.getString("choose_output_folder"))?.let {
                                viewModel.split(it)
                            }
                        }
                    })
                    add(JFXButton().apply {
                        visibleWhen(viewModel.selectedCount.gt(1))
                        managedWhen(visibleProperty())
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.CALL_MERGE, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        text = messages.getString("merge").toUpperCase()
                        action {
                            chooseDirectory(messages.getString("choose_output_folder"))?.let {
                                viewModel.merge(it)
                            }
                        }
                    })
                    add(JFXButton().apply {
                        visibleWhen(viewModel.selectedCount.gt(0))
                        managedWhen(visibleProperty())
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.DELETE, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        text = messages.getString("remove").toUpperCase()
                        action {
                            viewModel.deleteSelected()
                        }
                    })
                    add(JFXButton().apply {
                        visibleWhen(viewModel.hasSegments.and(viewModel.selectedCount.eq(0)))
                        managedWhen(visibleProperty())
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.ADD, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        isDisableVisualFocus = true
                        text = messages.getString("add").toUpperCase()
                        action {
                            chooseAndImportWav()
                        }
                    })
                    add(JFXButton().apply {
                        visibleWhen(viewModel.hasSegments.and(viewModel.selectedCount.eq(0)))
                        managedWhen(visibleProperty())
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.CLEAR_ALL, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        text = messages.getString("clear_all").toUpperCase()
                        isDisableVisualFocus = true
                        action {
                            viewModel.reset()
                        }
                    })
                    add(JFXButton().apply {
                        visibleWhen(viewModel.hasSegments)
                        managedWhen(visibleProperty())
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.SELECT_ALL, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        text = messages.getString("select_all").toUpperCase()
                        isDisableVisualFocus = true
                        action {
                            viewModel.selectAll()
                        }
                    })
                    add(JFXButton().apply {
                        addClass(MainViewStyles.actionBarButton)
                        graphic = MaterialIconView(MaterialIcon.LANGUAGE, "1.5em")
                        graphic?.addClass(MainViewStyles.actionBarIcon)
                        isDisableVisualFocus = true
                        action {
                            viewModel.toggleLangsShown()
                        }
                    })
                }
            }
            stackpane {
                vgrow = Priority.ALWAYS
                vbox {
                    hiddenWhen(viewModel.hasSegments)
                    addClass(MainViewStyles.bigDragTarget)
                    add(MaterialIconView(MaterialIcon.ADD, "2em"))
                    label(messages.getString("drop_here"))

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
                    visibleWhen(viewModel.hasSegments)
                    managedWhen(visibleProperty())
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

                            label {
                                addClass(MainViewStyles.segmentTitle)
                                text = messages.getString(segment.sourceMetadata.slug) + " " +
                                        "${segment.sourceMetadata.chapter.padStart(2, '0')}:" +
                                        segment.label.padStart(2, '0')
                            }

                            val takeNum = "t\\d+$".toRegex()
                                .find(segment.src.nameWithoutExtension)
                                ?.value?.substring(1)

                            if(takeNum !== null) {
                                label {
                                    addClass(MainViewStyles.segmentInfo)
                                    text = messages.getString("take") + " $takeNum"
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
                vbox {
                    visibleWhen(viewModel.langsShownProperty)
                    addClass(MainViewStyles.languagesList)
                    listview(viewModel.languages) {
                        prefHeight = items.size * 40.0
                        addClass(MainViewStyles.languagesListItems)
                        cellCache { language ->
                            hbox {
                                addClass(MainViewStyles.languageItem)
                                label(language.name)
                                setOnMouseClicked { viewModel.changeLanguage(language.slug) }
                            }
                        }
                    }
                }
            }

        }
    }

    fun chooseAndImportWav() {
        val wavFiles = chooseFile(
            messages.getString("choose_wav_file"),
            arrayOf(FileChooser.ExtensionFilter("WAV File", "*.wav")),
            mode = FileChooserMode.Single
        )
        for (file in wavFiles) {
            viewModel.importFile(file)
        }
    }
}
