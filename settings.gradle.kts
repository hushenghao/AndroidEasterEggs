@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
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
