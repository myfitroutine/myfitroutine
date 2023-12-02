pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        repositories {
            maven { setUrl("https://jitpack.io") }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        repositories {
            maven { setUrl("https://jitpack.io") }
        }
    }
}

rootProject.name = "myfitroutine"
include(":app")
