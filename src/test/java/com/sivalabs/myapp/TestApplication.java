package com.sivalabs.myapp;

import org.springframework.boot.SpringApplication;

public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.from(Application::main).with(TestcontainersConfig.class).run(args);
    }
}
