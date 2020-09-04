package org.bibletranslationtools.trchunkbrowser.app.mainview

import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class MainViewStyles : Stylesheet() {
    companion object {
        val root by cssclass()
        val actionBar by cssclass()
        val actionBarIcon by cssclass()
        val actionBarButton by cssclass()
        val barContent by cssclass()
        val segmentListItem by cssclass()
        val segmentTitle by cssclass()
        val segmentTime by cssclass()
        val segmentInfo by cssclass()
        val segmentList by cssclass()
        val bigDragTarget by cssclass()
        val jfxSnackbarContent by cssclass()
        val jfxSnackbarToast by cssclass()
        val jfxSnackbarAction by cssclass()
        val languagesList by cssclass()
        val languagesListItems by cssclass()
        val languageItem by cssclass()

        val bgColor = c("#ffffff")
        val primaryColor = c("#FF6C6C")
        val textColor = c("#646464")
        val shadowColor = c("d6d6d6")
        val faintBorderColor = c("#ccc")
        val snackbarColor = c("#2d2d2d")
        val columnWidth = 700.px
    }
    init {
        root {
            prefHeight = 500.px
            prefWidth = 1000.px
            backgroundColor += bgColor
            fontFamily = "Avenir"
            fontSize = 20.px
            textFill = textColor
            actionBar {
                maxHeight = 60.px
                minHeight = 60.px
                fontWeight = FontWeight.EXTRA_BOLD
                backgroundColor += primaryColor
                fontSize = 1.em
                effect = DropShadow(6.0, 0.0, 3.0, shadowColor)
                padding = box(10.px, 20.px)
                barContent {
                    alignment = Pos.CENTER_LEFT
                    label {
                        textFill = Color.WHITE
                    }
                    actionBarButton {
                        cursor = Cursor.HAND
                        textFill = Color.WHITE
                        actionBarIcon {
                            fill = Color.WHITE
                        }
                    }
                }
                alignment = Pos.CENTER_LEFT
            }

            segmentList {
                backgroundColor += Color.TRANSPARENT
                focusColor = Color.TRANSPARENT
                faintFocusColor = Color.TRANSPARENT
                maxWidth = columnWidth
                padding = box(20.px, 0.px, 0.px, 0.px)
                listCell {
                    backgroundColor += Color.TRANSPARENT
                }
                segmentListItem {
                    backgroundColor += Color.WHITE
                    backgroundRadius += box(4.px)
                    cursor = Cursor.HAND
                    effect = DropShadow(6.0, 0.0, 2.0, shadowColor)
                    padding = box(15.px)
                    spacing = 5.px
                    segmentTitle {
                        textFill = textColor
                        fontWeight = FontWeight.BOLD
                    }
                    segmentInfo {
                        textFill = faintBorderColor
                    }
                    segmentTime {
                        textFill = faintBorderColor
                    }
                    child(".jfx-check-box") {
                        unsafe("-jfx-checked-color", raw(primaryColor.css))
                        unsafe("-jfx-unchecked-color", raw(textColor.css))
                    }
                }
                scrollBar {

                }
            }
            languagesList {
                backgroundColor += Color.TRANSPARENT
                alignment = Pos.TOP_RIGHT

                languagesListItems {
                    maxWidth = 200.px
                    alignment = Pos.TOP_LEFT
                    backgroundColor += primaryColor
                    effect = DropShadow(6.0, 0.0, 3.0, shadowColor)

                    listCell {
                        backgroundColor += Color.TRANSPARENT
                    }

                    languageItem {
                        label {
                            fontWeight = FontWeight.BOLD
                            textFill = Color.WHITE
                        }
                        fill = textColor
                        cursor = Cursor.HAND
                    }
                }
            }
            bigDragTarget {
                backgroundColor += Color.TRANSPARENT
                cursor = Cursor.HAND
                alignment = Pos.CENTER
                spacing = 20.px
                label {
                    fontWeight = FontWeight.BOLD
                    textFill = textColor
                }
                child("*") {
                    fill = textColor
                }
            }
            jfxSnackbarContent {
                backgroundColor += snackbarColor
                backgroundRadius += box(4.px)
            }
            jfxSnackbarToast {
                textFill = Color.WHITE
            }
            jfxSnackbarAction {
                textFill = Color.WHITE
            }

            // Material design scroll bar
            scrollBar {
                backgroundColor += Color.TRANSPARENT
                padding = box(0.px, 4.px)
                prefWidth = 16.px
                thumb {
                    backgroundColor += Color.DARKGRAY
                    backgroundRadius += box(10.px)
                }
                incrementArrow {
                    visibility = FXVisibility.COLLAPSE
                }
                decrementArrow {
                    visibility = FXVisibility.COLLAPSE
                }
            }
        }
    }
}
