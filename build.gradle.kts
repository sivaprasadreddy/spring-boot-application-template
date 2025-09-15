import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import java.io.Reader
import java.util.*

plugins {
	java
	id("org.springframework.boot") version "3.1.3"
	id("io.spring.dependency-management") version "1.1.3"
	id("com.diffplug.spotless") version "6.21.0"
	id("com.gorylenko.gradle-git-properties") version "2.4.1"
	id("org.owasp.dependencycheck") version "8.3.1"
	jacoco
	id("org.sonarqube") version "4.4.1.3373"
}

group = "com.sivalabs"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.flywaydb:flyway-core")
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")
	implementation("io.micrometer:micrometer-tracing-bridge-otel")
	implementation("io.opentelemetry:opentelemetry-exporter-zipkin")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
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
	toolVersion = "0.8.10"
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
		palantirJavaFormat("2.30.0")
		formatAnnotations()
	}
}

// Reference doc : https://jeremylong.github.io/DependencyCheck/dependency-check-gradle/configuration.html
dependencyCheck {
	// the default artifact types that will be analyzed.
	analyzedTypes = listOf("jar")
	// CI-tools usually needs XML-reports, but humans needs HTML.
	formats = listOf("HTML", "JUNIT")
	// Specifies if the build should be failed if a CVSS score equal to or above a specified level is identified.
	// failBuildOnCVSS = 8.toFloat()
	// Output directory where the report should be generated
	outputDirectory = "$buildDir/reports/dependency-vulnerabilities"
	// specify a list of known issues which contain false-positives to be suppressed
	//suppressionFiles = ["$projectDir/config/dependencycheck/dependency-check-suppression.xml"]
	// Sets the number of hours to wait before checking for new updates from the NVD, defaults to 4.
	cveValidForHours = 24
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
		property("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/jacoco/test/jacoco.xml")
		property("sonar.junit.reportPaths", "$buildDir/test-results/test")
	}
}