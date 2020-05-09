package com.geekorum.build

import org.gradle.api.Project
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

internal fun Project.getGitSha1(): String? = runCommand("git rev-parse HEAD", workingDir = projectDir)?.trim()

internal fun Project.getHgSha1(): String? = runCommand("hg id --debug -i -r .", workingDir = projectDir)?.trim()

internal fun Project.getHgLocalRevisionNumber(): String? = runCommand("hg id -n -r .", workingDir = projectDir)?.trim()

fun Project.getChangeSet(): String {
    val git = rootProject.file(".git")
    val hg = rootProject.file(".hg")
    return when {
        git.exists() -> "git:${getGitSha1()}"
        hg.exists() -> "hg:${getHgSha1()}"
        else -> "unknown"
    }
}

/**
 * Compute a version code following this format : MmmPBBB
 * M is major, mm is minor, P is patch
 * BBB is build version number from hg
 */
fun Project.computeChangesetVersionCode(major: Int = 0, minor: Int = 0, patch: Int = 0): Int {
    val base = (major * 1000000) + (minor * 10000) + (patch * 1000)
    return base + (getHgLocalRevisionNumber()?.trim()?.toIntOrNull() ?: 0)
}

private fun Project.runCommand(
    command: String,
    workingDir: File = File("."),
    timeoutAmount: Long = 60,
    timeoutUnit: TimeUnit = TimeUnit.MINUTES
): String? {
    return try {
        ProcessBuilder(*command.split("\\s".toRegex()).toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start().apply {
                waitFor(timeoutAmount, timeoutUnit)
            }.inputStream.bufferedReader().readText()
    } catch (e: IOException) {
        logger.info("Unable to run command", e)
        null
    }
}
