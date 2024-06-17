// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
    }
}
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
    id("com.android.library") version "8.1.4" apply false
    alias(libs.plugins.googleGmsGoogleServices) apply false
    id("androidx.navigation.safeargs.kotlin") version "2.5.3" apply false
}

