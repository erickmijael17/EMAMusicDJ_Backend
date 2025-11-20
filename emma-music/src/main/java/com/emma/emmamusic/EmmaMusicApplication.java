package com.emma.emmamusic;

import com.emma.emmamusic.config.JwtProperties;
import com.emma.emmamusic.config.PythonProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, PythonProperties.class})
public class EmmaMusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmmaMusicApplication.class, args);
    }

}
