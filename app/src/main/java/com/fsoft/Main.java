package com.fsoft;

import java.io.FileInputStream;
import java.io.InputStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.AllArgsConstructor;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Todolist Premium API", version = "1.0", description = "API for Todolist Premium application"))
@AllArgsConstructor
public class Main {
  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }
}
