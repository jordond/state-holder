@file:Suppress("UNUSED_VARIABLE", "OPT_IN_USAGE")

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
}

kotlin {
    explicitApi()

    targetHierarchy.default()

    androidTarget {
        publishLibraryVariants("release")
    }

    js(IR) {
        browser()
        binaries.executable()
    }
    jvm()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { target ->
        target.binaries.framework {
            baseName = "stateholder-compose"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core"))

                implementation(compose.runtime)
                implementation(libs.kotlin.coroutines)
                api(libs.essenty.lifecycle)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.lifecycle)
                implementation(libs.androidx.lifecycle.compose)
            }
        }

        val iosMain by getting
        val jvmMain by getting
        val jsMain by getting

        val nonAndroidMain by creating {
            dependsOn(commonMain)
            iosMain.dependsOn(this)
            jvmMain.dependsOn(this)
            jsMain.dependsOn(this)
        }
    }
}

android {
    compileSdk = libs.versions.sdk.compile.get().toInt()
    namespace = "com.stateholder.extensions.compose"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = libs.versions.sdk.min.get().toInt()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        jvmToolchain(jdkVersion = 11)
    }
}