@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    buildscript {
        repositories {
            mavenCentral()
            maven { setUrl("https://storage.googleapis.com/r8-releases/raw") }
        }

        dependencies {
            // https://issuetracker.google.com/issues/349857231
            // AGP8.5.1 r8:8.5.27
            classpath("com.android.tools:r8:8.5.33")
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}

include(
    ":app",
    ":basic",
    ":jvm-basic",

    ":eggs:Gingerbread",
    ":eggs:Honeycomb",
    ":eggs:IceCreamSandwich",
    ":eggs:JellyBean",
    ":eggs:KitKat",
    ":eggs:Lollipop",
    ":eggs:Marshmallow",
    ":eggs:Nougat",
    ":eggs:Oreo",
    ":eggs:Pie",
    ":eggs:Q",
    ":eggs:R",
    ":eggs:S",
    ":eggs:Tiramisu",
    ":eggs:UpsideDownCake",
)
include(":script:compose-material-icons-generator")
include(":script:emoji-svg-xml-convertor")
rootProject.name = "Easter Eggs"
