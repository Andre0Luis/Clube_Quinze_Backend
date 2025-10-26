package br.com.clube_quinze.api.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public Clock utcClock() {
        return Clock.systemUTC();
    }
}
