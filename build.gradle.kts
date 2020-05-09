import com.geekorum.build.SourceLicenseCheckerPlugin
import com.geekorum.build.configureAnnotationProcessorDeps
import com.geekorum.build.setupGoogleContent

plugins {
    id("com.android.library") apply false
    kotlin("android") apply false
    kotlin("kapt") apply false
}


// some extra properties
extra["compileSdkVersion"] = "android-29"

allprojects {
    repositories {
        google().setupGoogleContent()
        jcenter()
    }
    apply<SourceLicenseCheckerPlugin>()
}

subprojects {
    group = "com.geekorum"
    version = "0.0.1"

    configureAnnotationProcessorDeps()
}

task("clean", type = Delete::class) {
    doLast {
        delete(buildDir)
    }
}


