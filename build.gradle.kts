plugins {
    java
    application
}

group = "com.tutorteam"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "com.tutorteam.ApplicationRunner"
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
}
