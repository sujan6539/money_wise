buildscript {


    dependencies {
        val kotlin_version = "1.8.20"
        val hilt_version = "2.48.1"
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.android.tools.build:gradle:7.2.0") // Update to the latest version
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10") // Update to the latest version

        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    kotlin("kapt") version "1.9.23"
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
}

