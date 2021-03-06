package org.unbrokendome.gradle.plugins.helm.command.tasks

import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*


/**
 * Builds the chart dependencies from the _requirements.lock_ or _requirements.yaml_ file. Corresponds to the
 * `helm dependency build` CLI command.
 *
 * This task will be skipped with `NO_SOURCE` if the chart does not contain either of the requirements files.
 */
open class HelmBuildDependencies : AbstractHelmCommandTask() {

    /**
     * The chart directory.
     */
    @get:Internal("Represented as part of other properties")
    val chartDir: DirectoryProperty =
        project.objects.directoryProperty()


    /**
     * A [FileCollection] containing the _requirements.yaml_ file if present. This is a read-only property.
     *
     * This is modeled as a [FileCollection] so the task will not fail if the file does not exist. The collection
     * will never contain more than one file.
     */
    @get:[InputFiles SkipWhenEmpty]
    @Suppress("unused")
    val requirementsYamlFile: FileCollection =
        chartDir.asFileTree.matching {
            it.include("requirements.yaml")
        }


    /**
     * A [FileCollection] containing the _requirements.lock_ file if present. This is a read-only property.
     *
     * This is modeled as a [FileCollection] so the task will not fail if the file does not exist. The collection
     * will never contain more than one file.
     */
    @get:[InputFiles SkipWhenEmpty]
    @Suppress("unused")
    val requirementsLockFile: FileCollection =
        chartDir.asFileTree.matching { it.include("requirements.lock") }


    /**
     * The _charts_ sub-directory; this is where sub-charts will be placed by the command (read-only).
     */
    @get:OutputDirectory
    @Suppress("unused")
    val subchartsDir: Provider<Directory> =
        chartDir.dir("charts")


    init {
        registerHelmHomeAsInputDir()
    }


    @TaskAction
    fun buildDependencies() {
        execHelm("dependency", "build") {
            args(chartDir)
        }
    }
}
