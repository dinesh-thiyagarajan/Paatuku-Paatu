pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Paatuku Paatu"
include(":app")
include(":mediaQuery")
include(":database")
include(":player")
include(":home")
include(":nowplaying")
include(":favorites")
include(":about")
