plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
}

group = "io.growthbook.sdk"
version = "1.0.0"

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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                api("com.squareup.okhttp3:okhttp:4.9.0")
                implementation("com.squareup.okhttp3:okhttp-sse:4.9.0")

                // https://mvnrepository.com/artifact/com.google.code.gson/gson
                //implementation("com.google.code.gson:gson:2.11.0")

                implementation("io.growthbook.sdk:Core:1.0.1")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("com.squareup.okhttp3:okhttp:4.9.0")
            }
        }
    }
}

android {
    compileSdk = 34
    namespace = "com.sdk.growthbook.okhttp_network_dispatcher"
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
            artifactId = "Gb-NetworkDispatcherOkHttp"
            version = "1.2.0"
        }
    }
}