package com.atacankullabci.immovin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    @Bean(name = "basicRestTemplate")
    public RestTemplate basicRestTemplate() {
        return new RestTemplate();
    }
}
