plugins {
    kotlin("jvm") version "2.0.20"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    kotlin("plugin.serialization") version "2.0.20"
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    group = "com.bitlake"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(platform("io.arrow-kt:arrow-stack:1.2.4"))
        implementation("io.arrow-kt:arrow-core")
        implementation("io.github.java-diff-utils:java-diff-utils:4.12")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.7.3")
        testImplementation(kotlin("test"))
        testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
    }

    tasks.test {
        useJUnitPlatform()
    }

    kotlin {
        jvmToolchain(21)
    }
}
