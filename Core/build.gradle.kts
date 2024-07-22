plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("maven-publish")
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

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.0")
            }
        }
    }
}

android {
    compileSdk = 34
    namespace = "com.sdk.growthbook.core"
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
            groupId = "com.github.utsmannn"
            artifactId = "Gb-Core"
            version = "1.2.0"
        }
    }
}