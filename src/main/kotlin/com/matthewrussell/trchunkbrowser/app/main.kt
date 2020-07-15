package com.matthewrussell.trchunkbrowser.app

import com.matthewrussell.trchunkbrowser.app.mainview.MainView
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch

class MainApp : App(MainView::class) {
    override fun start(stage: Stage) {
        stage.icons.add(
            Image(javaClass.getResource("/launcher.png").openStream())
        )
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    launch<MainApp>()
}
