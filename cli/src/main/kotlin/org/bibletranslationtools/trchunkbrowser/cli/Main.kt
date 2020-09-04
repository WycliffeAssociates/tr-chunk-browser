package org.bibletranslationtools.trchunkbrowser.cli

import picocli.CommandLine
import kotlin.system.exitProcess

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val exitCode = CommandLine(CommandLineApp()).execute(*args)
        exitProcess(exitCode)
    }
}
