plugins {
    application
    java
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.kiseru.asteroids.server.ApplicationRunner")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
