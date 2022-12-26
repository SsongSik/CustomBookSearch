// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.1.2" apply false
    id("com.android.library") version "7.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.6.21" apply false
    //Secrets Plugin
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.4.1" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10" apply false
    id("com.google.dagger.hilt.android") version "2.41" apply false
}

//task clean(type: Delete) {
//    delete rootProject.buildDir
//}

tasks.register("clean"){
    delete(rootProject.buildDir)
}