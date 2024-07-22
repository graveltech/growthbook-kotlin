pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        jcenter()
    }
}

rootProject.name = "GrowthBook"
include(":GrowthBook")
include(":Core")
include(":NetworkDispatcherKtor")
include(":NetworkDispatcherOkHttp")
include(":gbsample")
