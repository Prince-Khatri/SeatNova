package com.seatnova.bookingservice.service.impl;

import com.seatnova.bookingservice.service.UserValidationService;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Service("userDummyValidationService")
@Primary
public class UserDummyValidationImpl implements UserValidationService {
    private WebClient userClient;
    @Override
    public boolean validateUserId(UUID userId){
        return true;
    }

}
