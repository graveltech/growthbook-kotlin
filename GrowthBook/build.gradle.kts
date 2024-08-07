plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.4.20"
}

group = "io.growthbook.sdk"
version = "1.1.60"

kotlin {

    val ktorVersion = "2.1.2"
    val serializationVersion = "1.3.3"
    val kryptoVersion = "2.7.0"

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
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.0")
                implementation("com.ionspin.kotlin:bignum:0.3.3")
                implementation("com.soywiz.korlibs.krypto:krypto:$kryptoVersion")

                implementation(project(":Core"))
                api(
                    "org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion"
                )
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(
                    "org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion"
                )
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.startup:startup-runtime:1.1.1")
                implementation("com.soywiz.korlibs.krypto:krypto-android:$kryptoVersion")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-java:$ktorVersion")
                implementation("com.soywiz.korlibs.krypto:krypto-jvm:$kryptoVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation ("org.jetbrains.kotlin:kotlin-test-junit")
                implementation("com.soywiz.korlibs.krypto:krypto-jvm:$kryptoVersion")
            }
        }

    }

}

android {
    compileSdk = 34
    namespace = "com.sdk.growthbook"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21

        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

publishing {
    publications {
        withType<MavenPublication> {
            groupId = "com.github.graveltech"
            artifactId = "Growthbook"
            version = "1.2.1"
        }
    }
}