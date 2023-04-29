# Spring Boot Application Template
This repository is a template for creating a Spring Boot application with commonly used features pre-configured.

[![Maven Build](https://github.com/sivaprasadreddy/spring-boot-application-template/actions/workflows/ci-maven.yml/badge.svg)](https://github.com/sivaprasadreddy/spring-boot-application-template/actions/workflows/ci-maven.yml)
[![Gradle Build](https://github.com/sivaprasadreddy/spring-boot-application-template/actions/workflows/ci-gradle.yml/badge.svg)](https://github.com/sivaprasadreddy/spring-boot-application-template/actions/workflows/ci-gradle.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sivaprasadreddy_spring-boot-application-template&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=sivaprasadreddy_spring-boot-application-template)

## Features
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Flyway for DB migration
* Springdoc Open API
* JaCoCo code coverage check
* SonarQube code quality check
* OWASP Dependency Check
* JUnit 5
* Testcontainers for testing & Local Dev
* GitHub Actions
* DockerCompose Deployment

## Prerequisites
* Install Java using [SDKMAN](https://sdkman.io/)

    ```shell
    $ curl -s "https://get.sdkman.io" | bash
    $ source "$HOME/.sdkman/bin/sdkman-init.sh"
    $ sdk version
    $ sdk env install
    ```
* Install Docker : https://docs.docker.com/get-docker/

## Getting Started

```shell
$ git clone https://github.com/sivaprasadreddy/spring-boot-application-template.git
$ cd spring-boot-application-template
$ ./mvnw verify
```

To run the application from IDE, run `TestApplication.java` under `src/test/java`.

## How to?
This section describes how to perform various tasks.

### Code Formatting
The [spotless-maven-plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven) combined with 
[google-java-format](https://github.com/google/google-java-format) is used to format source code 
and is configured to automatically check code formatting while building the application.

```shell
$ ./mvnw spotless:apply    <- to format source code
$ ./mvnw spotless:check    <- to verify source code formatting
```

### JaCoCo Code Coverage
The [jacoco-maven-plugin](https://www.eclemma.org/jacoco/trunk/doc/maven.html) is used to verify the test code coverage.
If the expected code coverage is not met (default is set to 80%) then the build will fail.

```shell
$ ./mvnw verify
```

### SonarQube Quality Check
The [sonar-maven-plugin](https://docs.sonarqube.org/latest/analyzing-source-code/scanners/sonarscanner-for-maven/) is configured and 
is configured to run on [SonarCloud](https://sonarcloud.io/). 

You can configure the sonar properties in `sonar-project.properties` and run the sonar scan as follows:

```shell
$ ./mvnw initialize sonar:sonar -Dsonar.login=$SONAR_TOKEN
```

### OWASP Dependency Check
The [dependency-check-maven](https://jeremylong.github.io/DependencyCheck/dependency-check-maven/) is used to check
for security vulnerabilities in the used libraries.

```shell
$ ./mvnw dependency-check:check
```
You can see the generated report at `target/dependency-check-report.html`

### Create Docker Image
The [spring-boot-maven-plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/) provides 
the capability to create an [OCI image](https://github.com/opencontainers/image-spec) from a jar or war file using [Cloud Native Buildpacks](https://buildpacks.io/) (CNB).

```shell
$ ./mvnw spring-boot:build-image -DskipTests -Dspring-boot.build-image.imageName=$DOCKER_USERNAME/$DOCKER_IMAGE_NAME
```

### Run application using docker-compose
Once the application docker image is created, you can run the application using docker-compose as follows:

```shell
$ cd deployment/docker-compose
$ docker compose up -d
```

Now the application should be accessible at http://localhost:8080/
