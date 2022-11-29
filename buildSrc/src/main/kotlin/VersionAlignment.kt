/*
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2022 by Frederic-Charles Barthelery.
 *
 * This file is part of Geekdroid.
 *
 * Geekdroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Geekdroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Geekdroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.geekorum.build

import org.gradle.api.artifacts.ComponentMetadataContext
import org.gradle.api.artifacts.ComponentMetadataRule
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.ComponentMetadataHandler
import org.gradle.api.artifacts.dsl.DependencyHandler

private val componentsPlatform = mutableMapOf<ComponentMetadataHandler, MutableSet<String>>()

fun DependencyHandler.createComponentsPlatforms() {
    components.apply {
        getOrCreatePlatform(DaggerPlatform)
    }
}

private fun ComponentMetadataHandler.getOrCreatePlatform(platformFactory: PlatformFactory): String {
    val componentsSet = componentsPlatform.getOrPut(this) { mutableSetOf() }
    if (!componentsSet.contains(platformFactory.platformName)) {
        componentsSet.add(platformFactory.createPlatform(this))
    }
    return platformFactory.platformName
}

internal class DaggerPlatform {
    companion object : PlatformFactory("com.google.dagger:dagger-platform",
        AlignmentRule::class.java)

    open class AlignmentRule : SameGroupAlignmentRule(platformName, "com.google.dagger")
}

fun DependencyHandler.enforcedDaggerPlatform(version: String): Dependency {
    return enforcedPlatform("${components.getOrCreatePlatform(DaggerPlatform)}:$version")
}

open class PlatformFactory(
    internal val platformName: String,
    private val alignmentRule: Class<out ComponentMetadataRule>
) {
    fun createPlatform(components: ComponentMetadataHandler): String {
        components.all(alignmentRule)
        return platformName
    }
}

internal open class SameGroupAlignmentRule(
    private val platformName: String,
    private val group: String
) : ComponentMetadataRule {

    override fun execute(ctx: ComponentMetadataContext) {
        ctx.details.run {
            if (id.group == group) {
                belongsTo("$platformName:${id.version}")
            }
        }
    }

}
