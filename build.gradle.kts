import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jreleaser.model.Active

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.power.assert)
    alias(libs.plugins.dokka)
    alias(libs.plugins.versions)
    alias(libs.plugins.jreleaser)
    alias(libs.plugins.xemantic.conventions)
}

group = "com.xemantic.experiment"

xemantic {
    description = "A minimal example of Kotlin/Native and Rust integration"
    inceptionYear = "2026"
    applyAllConventions()
}

val kotlinTarget = KotlinVersion.fromVersion(libs.versions.kotlinTarget.get())

kotlin {

    compilerOptions {
        apiVersion = kotlinTarget
        languageVersion = kotlinTarget
        freeCompilerArgs.addAll(
            "-Xcontext-parameters",
            "-Xcontext-sensitive-resolution"
        )
        extraWarnings = true
        progressiveMode = true
        //optIn.addAll("add opt ins here")
    }

    // native, see https://kotlinlang.org/docs/native-target-support.html
    // tier 1
    macosX64 { binaries.executable() }
    macosArm64 { binaries.executable() }

    // tier 2
    linuxX64 { binaries.executable() }
    linuxArm64 { binaries.executable() }

    // tier 3
    mingwX64 { binaries.executable() }

    sourceSets {

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.xemantic.kotlin.test)
            }
        }

    }

}

repositories {
    mavenCentral()
}

powerAssert {
    functions = listOf(
        "com.xemantic.kotlin.test.assert",
        "com.xemantic.kotlin.test.have"
    )
}

// https://kotlinlang.org/docs/dokka-migration.html#adjust-configuration-options
dokka {
    pluginsConfiguration.html {
        footerMessage = xemantic.copyright
    }
}

val releaseAnnouncementSubject = """🚀 ${rootProject.name} $version has been released!"""
val releaseAnnouncement = """
$releaseAnnouncementSubject

${xemantic.description}

${xemantic.releasePageUrl}
""".trim()

jreleaser {

    announce {
        webhooks {
            create("discord") {
                active = Active.ALWAYS
                message = releaseAnnouncement
                messageProperty = "content"
                structuredMessage = true
            }
        }
        linkedin {
            active = Active.ALWAYS
            subject = releaseAnnouncementSubject
            message = releaseAnnouncement
        }
        bluesky {
            active = Active.ALWAYS
            status = releaseAnnouncement
        }
    }

}
