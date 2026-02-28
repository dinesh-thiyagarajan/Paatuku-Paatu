// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
    }

    dependencies {
        classpath(libs.spotless.plugin.gradle)
    }
}


plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    alias(libs.plugins.com.android.library) apply false
    id("com.diffplug.spotless") version "6.9.0" apply false
    alias(libs.plugins.ksp) apply false
}

subprojects {
    afterEvaluate {
        project.apply("${rootProject.projectDir}/spotless.gradle")
    }
}
