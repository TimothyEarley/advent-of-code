plugins {
    kotlin("jvm") version "1.6.0"
    id("com.diffplug.spotless") version "6.0.0"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:5.0.0")

}

tasks.test {
    useJUnitPlatform()
}


spotless {
    kotlin {
        ktlint("0.43.0").userData(mapOf("indent_style" to "tab"))
    }
}
