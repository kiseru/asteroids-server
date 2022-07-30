import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.slf4j:slf4j-simple:1.7.36")
}

application {
    mainClass.set("com.kiseru.asteroids.server.ApplicationKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}
