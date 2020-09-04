package org.bibletranslationtools.trchunkbrowser.app.controls.segment

import com.jfoenix.controls.JFXCheckBox
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.layout.AnchorPane
import tornadofx.*

class AudioSegment : AnchorPane() {
    var title: String by property()
    fun titleProperty() = getProperty(AudioSegment::title)

    var subtitle: String by property()
    fun subtitleProperty() = getProperty(AudioSegment::subtitle)

    var info: String by property()
    fun infoProperty() = getProperty(AudioSegment::info)

    var selected: Boolean by property()
    fun selectedProperty() = getProperty(AudioSegment::selected)

    private var checkbox: JFXCheckBox by singleAssign()

    init {
        importStylesheet<AudioSegmentStyles>()
        addClass(AudioSegmentStyles.segmentRoot)
        vbox {
            addClass(AudioSegmentStyles.segmentContent)
            anchorpaneConstraints {
                topAnchor = 0.0
                bottomAnchor = 0.0
                leftAnchor = 0.0
                rightAnchor = 0.0
            }
            label(titleProperty()) {
                addClass(AudioSegmentStyles.segmentTitle)
            }
            label(subtitleProperty()) {
                addClass(AudioSegmentStyles.segmentSubtitle)
            }
            label(infoProperty()) {
                addClass(AudioSegmentStyles.segmentInfo)
            }
        }
        checkbox = JFXCheckBox().apply {
            bind(this@AudioSegment.selectedProperty())
            addClass(AudioSegmentStyles.segmentCheck)
            anchorpaneConstraints {
                bottomAnchor = 10.0
                rightAnchor = 10.0
            }
        }
        add(checkbox)
        onMousePressed = EventHandler {
            checkbox.fireEvent(it)
        }
        onMouseReleased = EventHandler {
            checkbox.fireEvent(it)
        }
        onMouseClicked = EventHandler {
            checkbox.fireEvent(it)
        }
    }
}

fun EventTarget.audiosegment(op: AudioSegment.() -> Unit = {}) = AudioSegment().attachTo(this, op)
