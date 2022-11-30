@file:Suppress("GradlePackageUpdate")

plugins {
	kotlin("jvm")
	id("org.jetbrains.kotlinx.benchmark")
	id("org.jetbrains.kotlin.plugin.allopen")
}

repositories {
	mavenCentral()
}

dependencies {
	testImplementation(Testing.kotest.runner.junit5)
}

tasks.test {
	useJUnitPlatform()
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
	"benchmarkImplementation"("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:_")
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
