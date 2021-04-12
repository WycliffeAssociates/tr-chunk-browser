package org.bibletranslationtools.trchunkbrowser.app.controls.materialdialog

import com.jfoenix.controls.JFXButton
import javafx.scene.layout.VBox
import tornadofx.*

class DirectoryActionDialogContent : VBox() {
    var title: String by property()
    fun titleProperty() = getProperty(DirectoryActionDialogContent::title)

    var message: String by property()
    fun messageProperty() = getProperty(DirectoryActionDialogContent::message)

    var splitConfirmButtonText: String by property()
    fun splitConfirmButtonTextProperty() = getProperty(DirectoryActionDialogContent::splitConfirmButtonText)

    var mergeConfirmButtonText: String by property()
    fun mergeConfirmButtonTextProperty() = getProperty(DirectoryActionDialogContent::mergeConfirmButtonText)

    var cancelButtonText: String by property()
    fun cancelButtonTextProperty() = getProperty(DirectoryActionDialogContent::cancelButtonText)

    var splitConfirmButton: JFXButton by singleAssign()
    var mergeConfirmButton: JFXButton by singleAssign()
    var cancelButton: JFXButton by singleAssign()

    init {
        importStylesheet<MaterialDialogContentStyles>()
        addClass(MaterialDialogContentStyles.materialDialog)
        label(titleProperty()) {
            addClass(MaterialDialogContentStyles.title)
        }
        label(messageProperty()) {
            addClass(MaterialDialogContentStyles.message)
            isWrapText = true
        }
        spacer()
        hbox {
            addClass(MaterialDialogContentStyles.buttons)
            cancelButton = JFXButton().apply {
                textProperty().bind(cancelButtonTextProperty())
                addClass(MaterialDialogContentStyles.cancelButton)
                isDisableVisualFocus = true
            }
            splitConfirmButton = JFXButton().apply {
                textProperty().bind(splitConfirmButtonTextProperty())
                addClass(MaterialDialogContentStyles.confirmButton)
                isDisableVisualFocus = true
            }
            mergeConfirmButton = JFXButton().apply {
                textProperty().bind(mergeConfirmButtonTextProperty())
                addClass(MaterialDialogContentStyles.confirmButton)
                isDisableVisualFocus = true
            }
            add(cancelButton)
            add(splitConfirmButton)
            add(mergeConfirmButton)
        }
    }
}
