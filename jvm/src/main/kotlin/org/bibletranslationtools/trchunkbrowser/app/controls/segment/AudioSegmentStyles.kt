package org.bibletranslationtools.trchunkbrowser.app.controls.segment

import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*

class AudioSegmentStyles : Stylesheet() {
    companion object {
        val segmentRoot by cssclass()
        val segmentTitle by cssclass()
        val segmentSubtitle by cssclass()
        val segmentInfo by cssclass()
        val segmentCheck by cssclass()
        val segmentContent by cssclass()

        val bgColor = Color.BLACK //c("#13162C")
        val fgColor = c("#7097C4")
        val edgeColor = c("#3B416C")
    }
    init {
        Font.loadFont(ClassLoader.getSystemResourceAsStream("BPdotsSquare.otf"), 10.0)
        Font.loadFont(ClassLoader.getSystemResourceAsStream("Patopian 1986.ttf"), 10.0)

        segmentRoot {
            maxHeight = 150.px
            minHeight = 150.px
            prefWidth = 200.px
            backgroundColor += bgColor
            borderColor += box(edgeColor)
            borderRadius += box(5.px)
            backgroundRadius += box(5.px)
//            backgroundImage += ClassLoader.getSystemResource("segment-bg.png").toURI()
            cursor = Cursor.HAND
            effect = DropShadow(12.0, 0.0, 3.0, Color.BLACK)

            segmentContent {
                alignment = Pos.TOP_LEFT
                padding = box(10.px, 10.px)

                segmentTitle {
                    fontSize = 28.px
                    fontFamily = "BPdotsSquares"
                    textFill = fgColor
                }

                segmentSubtitle {
                    fontSize = 20.px
                    fontFamily = "Patopian 1986"
                    textFill = fgColor
                }

                segmentInfo {
                    fontFamily = "BPdotsSquares"
                    fontSize = 20.px
                    textFill = fgColor
                    wrapText = true
                }
            }

            segmentCheck {
                unsafe("-jfx-checked-color", raw(fgColor.css))
                unsafe("-jfx-unchecked-color", raw(fgColor.css))
                select(".mark") {
                    unsafe("-fx-border-color", raw(bgColor.css))
                }
            }
        }
    }
}
