package com.example.inventory_management.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.*;


@Configuration
@OpenAPIDefinition(
  info =@Info(
    title = "Demo API",
    version = "Version 1.0",
    contact = @Contact(
      name = "StudyEasy", email = "admin@studyeasy.org", url = "https://studyeasy.org"
    ),
    license = @License(
      name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"
    ),
    termsOfService = "https://studyeasy.org/",
    description = "Spring Boot RestFul API Demo by Chaand"
  )
)
public class SwaggerConfig {

}
