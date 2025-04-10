package ru.yandex.practicum.filmorate.config;

import org.zalando.logbook.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogbookConfig {

    @Bean
    public Logbook logbook() {
        return Logbook.builder()
                .requestFilter(request -> {
                    request.getHeaders().remove("accept");
                    request.getHeaders().remove("host");
                    request.getHeaders().remove("Content-Type");
                    request.getHeaders().remove("postman-token");
                    request.getHeaders().remove("accept-encoding");
                    request.getHeaders().remove("connection");
                    request.getHeaders().remove("content-length");
                    return request;
                })
                .responseFilter(response -> {
                    response.getHeaders().remove("connection");
                    response.getHeaders().remove("Content-Type");
                    response.getHeaders().remove("Transfer-Encoding");
                    response.getHeaders().remove("Keep-Alive");
                    return response;
                })
                .build();
    }
}
