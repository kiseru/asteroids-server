plugins {
    java
    kotlin("jvm") version "2.1.10"
    application
}

group = "ru.kiseru"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "ru.kiseru.ApplicationRunner"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
}

tasks.test {
    useJUnitPlatform()
}
