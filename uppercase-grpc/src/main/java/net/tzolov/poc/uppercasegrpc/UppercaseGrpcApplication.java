package net.tzolov.poc.uppercasegrpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Locale;
import java.util.function.Function;

@SpringBootApplication
public class UppercaseGrpcApplication {

    public static void main(String[] args) {
        SpringApplication.run(UppercaseGrpcApplication.class, args);
    }

    @Bean
    public Function<String, String> uppercase() {
        //return String::toUpperCase;
        return payload -> payload.toUpperCase();
    }
}
