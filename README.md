# Spring Boot Application Template
This repository is a template for creating a Spring Boot application with commonly used features pre-configured.

## Features
* Spring Boot
* Spring Data JPA
* Postgres
* FlywayDB
* GitHub Actions
* SonarQube Code Scan
* JaCoCo Code Coverage Check
* OWASP Dependency Check
* Springdoc Open API
* JUnit 5
* Testcontainers for testing & Local Dev

## Prerequisites
* Install Java using SDKMAN

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

To run the application, run `TestApplication.java` under `src/test/java`
