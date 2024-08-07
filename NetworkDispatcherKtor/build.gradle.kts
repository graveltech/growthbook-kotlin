plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.4.20"
}

group = "io.growthbook.sdk"
version = "1.0.2"

kotlin {
    android {
        publishLibraryVariants("release")
    }

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    val ktorVersion = "2.1.2"
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.0")

                api("io.ktor:ktor-client-core:$ktorVersion")
                api("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                implementation(project(":Core"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("io.ktor:ktor-client-android:$ktorVersion")
            }
        }
    }
}

android {
    compileSdk = 34
    namespace = "com.sdk.growthbook.default_network_dispatcher"
    defaultConfig {
        minSdk = 21
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
        debug {
            isMinifyEnabled = false
        }
    }
}
/**
 * Publishing Task for MavenCentral
 */
publishing {
    publications {
        withType<MavenPublication> {
            groupId = "com.github.graveltech"
            artifactId = "Gb-NetworkDispatcherKtor"
            version = "1.2.1"
        }
    }
}