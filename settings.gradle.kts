@file:Suppress("UnstableApiUsage")

pluginManagement {
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
    versionCatalogs {
        create("deps") {
            // default path: gradle/libs.versions.toml
            from(files("libs.versions.toml"))
        }
    }
}

include(
    ":app",
    ":basic",
    ":eggs:Gingerbread",
    ":eggs:Honeycomb",
    ":eggs:Ice_Cream_Sandwich",
    ":eggs:Jelly_Bean",
    ":eggs:KitKat",
    ":eggs:Lollipop",
    ":eggs:Marshmallow",
    ":eggs:Nougat",
    ":eggs:Oreo",
    ":eggs:Pie",
    ":eggs:Q",
    ":eggs:R",
    ":eggs:S",
    ":eggs:T",
    ":eggs:U",
)
rootProject.name = "Easter Eggs"
