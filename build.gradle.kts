import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("java")
	id("org.springframework.boot") version "4.0.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.diffplug.spotless") version "8.1.0"
	id("com.gorylenko.gradle-git-properties") version "2.5.4"
    id("jacoco")
	id("org.sonarqube") version "7.1.0.6387"
    id("net.ltgt.errorprone") version "4.3.0"
}

group = "com.sivalabs"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.flywaydb:flyway-database-postgresql")
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.0")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    errorprone("com.google.errorprone:error_prone_core:2.45.0") // https://github.com/google/error-prone
    errorprone("com.uber.nullaway:nullaway:0.12.12")
}

tasks.withType<JavaCompile> {
    options.errorprone  {
        disableAllChecks = true // Other error prone checks are disabled
        option("NullAway:OnlyNullMarked", "true") // Enable nullness checks only in null-marked code
        error("NullAway") // bump checks from warnings (default) to errors
        option("NullAway:JSpecifyMode", "true") // https://github.com/uber/NullAway/wiki/JSpecify-Support
    }
    // Keep a JDK 25 baseline
    options.release = 25
}

tasks.withType<Test> {
	useJUnitPlatform()

	testLogging {
		events = setOf(PASSED, FAILED, SKIPPED)
		showStandardStreams = true
		exceptionFormat = FULL
	}
	finalizedBy(tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
}

jacoco {
	toolVersion = "0.8.14"
	//reportsDirectory.set(layout.buildDirectory.dir("customJacocoReportDir"))
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(false)
		csv.required.set(false)
		html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco"))
	}
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			element = "BUNDLE"
			//includes = listOf("com.sivalabs.*")

			limit {
				counter = "LINE"
				value = "COVEREDRATIO"
				minimum = "0.8".toBigDecimal()
			}
		}
	}
}

gitProperties {
	failOnNoGitDirectory = false
	keys = listOf("git.branch",
		"git.commit.id.abbrev",
		"git.commit.user.name",
		"git.commit.message.full")
}

spotless {
	java {
		importOrder()
		removeUnusedImports()
		palantirJavaFormat("2.83.0")
		formatAnnotations()
	}
}

sonarqube {
	properties {
		property("sonar.sourceEncoding", "UTF-8")
		property("sonar.projectKey", "sivaprasadreddy_spring-boot-application-template")
		property("sonar.projectKey", "spring-boot-application-template")
		property("sonar.organization", "sivaprasadreddy-github")
		property("sonar.host.url", "https://sonarcloud.io")
		property("sonar.sources", "src/main/java")
		property("sonar.tests", "src/test/java")
		property("sonar.exclusions", "src/main/java/**/config/*.*,src/main/java/**/entities/*.*,src/main/java/**/models/*.*,src/main/java/**/exceptions/*.*,src/main/java/**/utils/*.*,src/main/java/**/*Application.*")
		property("sonar.test.inclusions", "**/*Test.java,**/*IntegrationTest.java,**/*IT.java")
		property("sonar.java.codeCoveragePlugin", "jacoco")
		property("sonar.coverage.jacoco.xmlReportPaths", "build/jacoco/test/jacoco.xml")
		property("sonar.junit.reportPaths", "build/test-results/test")
	}
}
