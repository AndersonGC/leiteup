// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        // Adiciona o classpath do Safe Args
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.0") // Use a versão mais recente disponível
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}
