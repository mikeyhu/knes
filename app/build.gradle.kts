/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/6.7.1/userguide/building_java_projects.html
 */

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.5.31"

    // Apply the application plugin to add support for building a CLI application in Java.
    application

    id("com.github.johnrengelman.shadow") version "7.0.0"

}

repositories {
    // Use JCenter for resolving dependencies.
    jcenter()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:29.0-jre")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

application {
    // Define the main class for the application.
    mainClass.set("net.chompsoftware.knes.AppKt")
}

tasks {
    "test"(Test::class) {
        useJUnitPlatform()
    }
}

tasks {
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "com.example.ApplicationKt"))
        }
    }
}


