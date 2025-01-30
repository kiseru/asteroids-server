plugins {
    kotlin("jvm") version "2.1.10"
    application
    java
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.kiseru.asteroids.server.MainKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
