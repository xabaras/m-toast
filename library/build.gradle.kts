import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

group = "it.xabaras"
version = "1.0.0"

kotlin {
    jvm()
    androidLibrary {
        namespace = "it.xabaras.mtoast"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava() // enable java compilation support
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(
                    JvmTarget.JVM_11
                )
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "library", version.toString())

    pom {
        name = "mToast"
        description = "A compose multiplatform library providing a simple toast messages implementation for multiplatform apps"
        inceptionYear = "2026"
        url = "https://github.com/xabaras/m-toast"
        licenses {
            license {
                name = "Apache-2.0 license "
                url = "https://github.com/xabaras/m-toast/blob/main/LICENSE"
                distribution = "repo"
            }
        }
        developers {
            developer {
                id = "xabaras"
                name = "Paolo Montalto"
                url = "https://www.xabaras.dev"
            }
        }
        scm {
            url = "https://github.com/xabaras/m-toast"
            connection = "scm:git:git://github.com/xabaras/m-toast.git"
            developerConnection = "scm:git:ssh://git@github.com/xabaras/m-toast.git"
        }
    }
}
