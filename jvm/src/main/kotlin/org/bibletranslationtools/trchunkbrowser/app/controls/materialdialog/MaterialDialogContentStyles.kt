package org.bibletranslationtools.trchunkbrowser.app.controls.materialdialog

import org.bibletranslationtools.trchunkbrowser.app.mainview.MainViewStyles
import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import tornadofx.*

class MaterialDialogContentStyles : Stylesheet() {
    companion object {
        val materialDialog by cssclass()
        val buttons by cssclass()
        val confirmButton by cssclass()
        val cancelButton by cssclass()
        val title by cssclass()
        val message by cssclass()

        val titleColor = c("#646464")
        val messageColor = c("#646464")
    }
    init {
        materialDialog {
            backgroundRadius += box(8.px)
            padding = box(20.px)
            spacing = 20.px
            title {
                fontWeight = FontWeight.BOLD
                textFill = titleColor
            }
            message {
                textFill = messageColor
            }
            buttons {
                alignment = Pos.CENTER_RIGHT
                confirmButton {
                    textFill = MainViewStyles.primaryColor
                }
                cancelButton {
                    textFill = MainViewStyles.faintBorderColor
                }
            }
        }
    }
}
