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
    ":eggs:S"
)
rootProject.name = "Easter Eggs"

enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("deps") {
            // default path: gradle/libs.versions.toml
            from(files("libs.versions.toml"))
        }
    }
}
