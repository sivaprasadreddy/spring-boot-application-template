import nu.studer.gradle.jooq.JooqEdition
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
	java
	id("org.springframework.boot") version "3.1.0-SNAPSHOT"
	id("io.spring.dependency-management") version "1.1.0"
	id("com.diffplug.spotless") version "6.18.0"
	id("com.gorylenko.gradle-git-properties") version "2.4.1"
	id("org.owasp.dependencycheck") version "8.2.1"
	jacoco
	id("org.sonarqube") version "4.0.0.2929"
	id("nu.studer.jooq") version "8.2"
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
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	implementation("org.flywaydb:flyway-core")
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")
	implementation("io.micrometer:micrometer-tracing-bridge-otel")
	implementation("io.opentelemetry:opentelemetry-exporter-zipkin")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")

	jooqGenerator("org.postgresql:postgresql:42.5.1")
	jooqGenerator("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")
	jooqGenerator("org.testcontainers:postgresql:1.18.0")
}

val dbUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/postgres"
val dbUser = System.getenv("DB_USER") ?: "postgres"
val dbPassword = System.getenv("DB_PASSWORD") ?: "postgres"
val dbSchema = "public"
val dbDriver = "org.postgresql.Driver"
val jooqVersion = "3.18.3"

jooq {
	version.set(jooqVersion)
	edition.set(JooqEdition.OSS)

	configurations {
		create("main") {
			generateSchemaSourceOnCompilation.set(false) //set to true to remove existing jooq code and regenerate

			jooqConfiguration.apply {
				logging = org.jooq.meta.jaxb.Logging.WARN
				jdbc.apply {
					driver = dbDriver
					url = dbUrl
					user = dbUser
					password = dbPassword
				}
				generator.apply {
					name = "org.jooq.codegen.DefaultGenerator"
					database.apply {
						name = "org.jooq.meta.postgres.PostgresDatabase"
						inputSchema = dbSchema
					}
					generate.apply {
						isRecords = true
						isDaos = true
						isSpringAnnotations = true
						isPojosEqualsAndHashCode = true
						isJavaTimeTypes = true
						isValidationAnnotations = false
					}
					target.apply {
						packageName = "com.sivalabs.bookmarks.jooq"
						directory = "src/main/jooq"
					}
					/*strategy.apply {
						matchers.apply {
							tables.apply {
								get(0).apply {
									tableClass.apply {
										pojoClass.apply {
											transform = MatcherTransformType.PASCAL
											expression = "JOOQ_\$0"
										}
										daoClass.apply {
											transform = MatcherTransformType.PASCAL
											expression = "\$0_Repository"
										}
									}
								}
							}
						}
					}*/
				}
			}
		}
	}
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
	toolVersion = "0.8.9"
	//reportsDirectory.set(layout.buildDirectory.dir("customJacocoReportDir"))
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	classDirectories.setFrom(
		sourceSets.main.get().output.asFileTree.matching {
			exclude("com/sivalabs/bookmarks/jooq/**")
		}
	)
	reports {
		xml.required.set(false)
		csv.required.set(false)
		html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco"))
	}
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			classDirectories.setFrom(sourceSets.main.get().output.asFileTree.matching {
				exclude("com/sivalabs/bookmarks/jooq/**")
			})
			element = "BUNDLE"

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
		target("src/*/java/**/*.java")
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
		property("sonar.exclusions", "src/main/java/com/sivalabs/bookmarks/jooq/**")
		property("sonar.test.inclusions", "**/*Test.java,**/*Tests.java,**/*IntegrationTest.java,**/*IT.java")
		property("sonar.java.codeCoveragePlugin", "jacoco")
		property("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/jacoco/test/jacoco.xml")
		property("sonar.junit.reportPaths", "$buildDir/test-results/test")
	}
}