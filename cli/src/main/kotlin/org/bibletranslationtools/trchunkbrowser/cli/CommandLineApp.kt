package org.bibletranslationtools.trchunkbrowser.cli

import picocli.CommandLine.Option
import picocli.CommandLine.Command
import java.io.File
import java.util.logging.Level

@Command(name = "chunker/dechunker")
class CommandLineApp() : Runnable {

    private val controller = CommandLineController()

    @Option(names = ["-s", "--split"], description = ["Split file(s) into verses"])
    private var split: Boolean = false

    @Option(names = ["-m", "--merge"], description = ["Merge files into one file"])
    private var merge: Boolean = false

    @Option(names = ["-f", "--files"], description = ["One or more input files"], arity = "1..*")
    private val files: List<File> = listOf()

    @Option(names = ["-d", "--dir"], description = ["Input directory to split files in place, recursively"])
    private val inputDir: File? = null

    @Option(
        names = ["-o", "--out"],
        description = [
            "Output directory to save result files to.",
            "Required for split (-s) and merge (-m) actions.",
            "Optional for dir split (-d)"
        ]
    )
    private val outputDir: File? = null

    @Option(names = ["-h", "--help"], usageHelp = true, description = ["display a help"])
    private var helpRequested = false

    private fun execute() {
        when {
            inputDir != null -> {
                controller.splitDirectory(inputDir, outputDir)
            }
            split -> {
                outputDir?.let {
                    controller.importFiles(files)
                    controller.split(outputDir)
                } ?: run {
                    controller.logger.log(Level.SEVERE, "Output directory is not defined or does not exist")
                }
            }
            merge -> {
                outputDir?.let {
                    controller.importFiles(files)
                    controller.merge(outputDir)
                } ?: run {
                    controller.logger.log(Level.SEVERE, "Output directory is not defined or does not exist")
                }
            }
        }
    }

    override fun run() {
        execute()
    }
}
