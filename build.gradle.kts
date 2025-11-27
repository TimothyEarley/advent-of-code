@file:Suppress("GradlePackageUpdate")

import de.fayard.refreshVersions.core.versionFor

plugins {
	kotlin("jvm")
	id("org.jetbrains.kotlinx.benchmark")
	id("org.jetbrains.kotlin.plugin.allopen")
	id("com.diffplug.spotless")
}

repositories {
	mavenCentral()
	maven("https://repo.kotlin.link")
	maven {
		url = uri("https://jitpack.io")
	}
}

dependencies {
	testImplementation(Testing.kotest.runner.junit5)
	implementation("cc.ekblad.konbini:konbini:_")
	implementation(KotlinX.coroutines.core)
	implementation("space.kscience:kmath-polynomial:_")
	implementation("tools.aqua:z3-turnkey:_")
}

tasks.test {
	useJUnitPlatform()
}

kotlin {
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of("25"))
	}

	compilerOptions {
		freeCompilerArgs.add("-Xcontext-parameters")
	}
}

spotless {
	kotlin {
		ktlint(versionFor("com.pinterest.ktlint:ktlint-core:_"))
			.editorConfigOverride(
				mapOf(
					"ktlint_standard_trailing-comma-on-call-site" to "disabled"
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
			iterationTimeUnit = "s"
			mode = "AverageTime"
			outputTimeUnit = "MICROSECONDS"
		}
	}
}