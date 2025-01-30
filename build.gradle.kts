plugins {
    kotlin("jvm") version "2.1.10"
    application
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
}

application {
    mainClass.set("com.kiseru.asteroids.server.MainKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
