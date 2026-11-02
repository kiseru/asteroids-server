plugins {
    java
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

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
}
