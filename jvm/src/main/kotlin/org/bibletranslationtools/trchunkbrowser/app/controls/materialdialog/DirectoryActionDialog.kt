package org.bibletranslationtools.trchunkbrowser.app.controls.materialdialog

import com.jfoenix.controls.JFXButton
import javafx.scene.layout.VBox
import tornadofx.*

class DirectoryActionDialog : VBox() {
    var title: String by property()
    fun titleProperty() = getProperty(MaterialDialogContent::title)

    var message: String by property()
    fun messageProperty() = getProperty(MaterialDialogContent::message)

    var confirmButtonText: String by property()
    fun confirmButtonTextProperty() = getProperty(MaterialDialogContent::confirmButtonText)

    var mergeConfirmButtonText: String by property()
    fun mergeConfirmButtonTextProperty() = getProperty(DirectoryActionDialog::mergeConfirmButtonText)

    var cancelButtonText: String by property()
    fun cancelButtonTextProperty() = getProperty(MaterialDialogContent::cancelButtonText)

    var confirmButton: JFXButton by singleAssign()
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
            confirmButton = JFXButton().apply {
                textProperty().bind(confirmButtonTextProperty())
                addClass(MaterialDialogContentStyles.confirmButton)
                isDisableVisualFocus = true
            }
            mergeConfirmButton = JFXButton().apply {
                textProperty().bind(mergeConfirmButtonTextProperty())
                addClass(MaterialDialogContentStyles.confirmButton)
                isDisableVisualFocus = true
            }
            add(cancelButton)
            add(confirmButton)
            add(mergeConfirmButton)
        }
    }
}