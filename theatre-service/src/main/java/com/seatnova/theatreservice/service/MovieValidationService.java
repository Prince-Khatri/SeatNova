package com.seatnova.theatreservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieValidationService {

    private final WebClient movieServiceClient;

    public void validateMovieExists(UUID movieId) {
        try {
            movieServiceClient.get()
                    .uri("/movies/{movieId}", movieId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            System.out.println("Movie Validated");
        } catch (WebClientResponseException.NotFound exception) {
            throw new RuntimeException("Movie not found with id: " + movieId, exception);
        } catch (WebClientResponseException | WebClientRequestException exception) {
            throw new RuntimeException("Unable to validate movie with id: " + movieId, exception);
        }
    }
}
