package com.jwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.models.Teamsheet;
import com.jwt.security.jwt.AuthTokenFilter;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Log
@Configuration
public class CustomSecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {  return new AuthTokenFilter();    }

//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        // Register the custom deserializer for Teamsheet objects
//        objectMapper.registerSubtypes(Teamsheet.class);
//        return objectMapper;
//    }

}