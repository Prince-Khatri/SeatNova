package com.seatnova.bookingservice.service.impl;

import com.seatnova.bookingservice.service.TheatreValidationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Service
public class TheatreValidationServiceImpl implements TheatreValidationService {
    private WebClient theatreClient;

    public TheatreValidationServiceImpl(WebClient.Builder webClientBuilder){

        this.theatreClient = webClientBuilder
                .baseUrl("http://THEATRE-SERVICE")
                .build();
    }

    @Override
    public boolean validateSeatId(UUID seatId){
        try {

            boolean flag= Boolean.TRUE.equals(this.theatreClient
                    .get()
                    .uri("api/v1/shows/{seatId}/validate", seatId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
            System.out.println("result: "+flag);
            if(!flag) throw new EntityNotFoundException("No Seat with SeatId:"+seatId+" Inside validateSeat");
            return flag;
        }
        catch (WebClientResponseException e){
            if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                throw new RuntimeException("Seat Not Found "+ seatId);
            }
            if(e.getStatusCode() == HttpStatus.BAD_REQUEST){
                throw new RuntimeException("Bad Request "+ seatId);
            }
        }
        return false;

    }

    @Override
    public boolean validateShowId(UUID showId){
        try {
            return Boolean.TRUE.equals(this.theatreClient
                    .get()
                    .uri("/api/v1/shows/{showId}/validate", showId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        }
        catch (WebClientResponseException e){
            if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                throw new RuntimeException("Show Not Found "+ showId);
            }
            if(e.getStatusCode() == HttpStatus.BAD_REQUEST){
                throw new RuntimeException("Bad Request "+ showId);
            }
        }
        return false;

    }
}
