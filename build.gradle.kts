plugins {
    kotlin("jvm") version "1.6.0"
    id("com.diffplug.spotless") version "6.0.0"
    id("org.jetbrains.kotlinx.benchmark") version "0.3.1"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.6.0"
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
        ktlint("0.43.0").userData(
            mapOf(
                "indent_style" to "tab",
                "disabled_rules" to "parameter-list-wrapping"
            )
        )
    }
}

// Benchmark stuff

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

sourceSets {
    create("benchmark") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

dependencies {
    "benchmarkImplementation"("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.3.1")
}

benchmark {
    targets {
        register("benchmark")
    }
    configurations {
        getByName("main") {
            warmups = 2
            iterations = 5
            iterationTime = 3
            mode = "AverageTime"
            outputTimeUnit = "MICROSECONDS"
        }
    }
}

// Workaround for https://github.com/Kotlin/kotlinx-benchmark/issues/39
afterEvaluate {
    tasks.named<org.gradle.jvm.tasks.Jar>("benchmarkBenchmarkJar") {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
