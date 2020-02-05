package com.matthewrussell.trchunkbrowser.domain

import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder
import org.apache.commons.configuration2.builder.fluent.Configurations
import java.io.File
import java.io.FileOutputStream

object Properties {
    val builder: FileBasedConfigurationBuilder<PropertiesConfiguration>
    val config: PropertiesConfiguration

    init {
        val configs = Configurations()
        val propertiesFile = File("config.properties")

        if(!propertiesFile.exists()) {
            val os = FileOutputStream(propertiesFile)
            os.write("".toByteArray())
            os.close()
        }

        builder = configs.propertiesBuilder(propertiesFile)
        config = builder.configuration
    }


}