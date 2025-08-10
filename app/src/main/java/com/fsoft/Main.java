package com.fsoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Todolist Premium API", version = "1.0", description = "API for Todolist Premium application"))
public class Main {
  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }
}
