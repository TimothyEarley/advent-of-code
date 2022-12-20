@file:Suppress("GradlePackageUpdate")

plugins {
	kotlin("jvm")
	id("org.jetbrains.kotlinx.benchmark")
	id("org.jetbrains.kotlin.plugin.allopen")
}

repositories {
	mavenCentral()
	maven {
		url = uri("https://jitpack.io")
	}
}

dependencies {
	testImplementation(Testing.kotest.runner.junit5)
	implementation("cc.ekblad.konbini:konbini:_")
	implementation(KotlinX.coroutines.core)
}

tasks.test {
	useJUnitPlatform()
}

kotlin {
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of("17"))
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