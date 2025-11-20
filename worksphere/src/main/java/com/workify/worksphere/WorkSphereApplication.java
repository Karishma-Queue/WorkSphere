package com.workify.worksphere;

import com.workify.worksphere.config.properties.CloudinaryProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(CloudinaryProps.class)

@SpringBootApplication
public class WorkSphereApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkSphereApplication.class, args);
	}

}
