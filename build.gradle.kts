buildscript {
    repositories {
        // Check that you have the following line (if not, add it)
        google()  // Google's Maven repository
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
    }
}



// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    alias(libs.plugins.google.gms.google.services) apply false
}