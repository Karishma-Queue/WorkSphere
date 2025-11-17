package com.workify.worksphere.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.workify.worksphere.config.properties.CloudinaryProps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

  private final CloudinaryProps props;

  public CloudinaryConfig(CloudinaryProps props) {
    this.props = props;
  }

  @Bean
    public Cloudinary cloudinary()
    {

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", props.cloudName(),
                "api_key", props.apiKey(),
                "api_secret", props.apiSecret()
        ));

    }
}
