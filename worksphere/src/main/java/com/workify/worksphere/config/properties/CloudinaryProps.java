package com.workify.worksphere.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloudinary")
public record CloudinaryProps(String cloudName, String apiKey, String apiSecret) {}
