package com.seatnova.bookingservice.service.impl;

import com.seatnova.bookingservice.service.UserValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Service("userValidationService")
public class UserValidationServiceImpl implements UserValidationService {
    private WebClient userClient;

    public UserValidationServiceImpl(WebClient.Builder webClientBuilder){
        this.userClient = webClientBuilder
                .baseUrl("http://USER-SERVICE")
                .build();
    }

    @Override
    public boolean validateUserId(UUID userId){
        try {
            return Boolean.TRUE.equals(this.userClient
                    .get()
                    .uri("/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        }
        catch (WebClientResponseException e){
            if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                throw new RuntimeException("User Not Found "+ userId);
            }
            if(e.getStatusCode() == HttpStatus.BAD_REQUEST){
                throw new RuntimeException("Bad Request "+ userId);
            }
        }
        return false;
    }

}
