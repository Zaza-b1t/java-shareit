package ru.practicum.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.booking.BookingClient;
import ru.practicum.gateway.item.ItemClient;
import ru.practicum.gateway.request.ItemRequestClient;
import ru.practicum.gateway.user.UserClient;

@Configuration
public class ClientConfig {

    @Value("${shareit-server.url:http://localhost:9090}")
    private String serverUrl;

    @Bean
    public BookingClient bookingClient(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .uriTemplateHandler(
                        new DefaultUriBuilderFactory(serverUrl + BookingClient.API_PREFIX)
                )
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();

        return new BookingClient(restTemplate);
    }

    @Bean
    public ItemClient itemClient(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .uriTemplateHandler(
                        new DefaultUriBuilderFactory(serverUrl + ItemClient.API_PREFIX)
                )
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();

        return new ItemClient(restTemplate);
    }

    @Bean
    public ItemRequestClient itemRequestClient(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .uriTemplateHandler(
                        new DefaultUriBuilderFactory(serverUrl + ItemRequestClient.API_PREFIX)
                )
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();

        return new ItemRequestClient(restTemplate);
    }

    @Bean
    public UserClient userClient(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .uriTemplateHandler(
                        new DefaultUriBuilderFactory(serverUrl + UserClient.API_PREFIX)
                )
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();

        return new UserClient(restTemplate);
    }
}
