
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.30")
        //classpath(deps.bundles.classpath)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}