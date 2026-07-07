package com.seatnova.theatreservice.controller;

import com.seatnova.theatreservice.controller.dto.ScreenRequest;
import com.seatnova.theatreservice.controller.dto.ScreenResponse;
import com.seatnova.theatreservice.controller.dto.SeatRequest;
import com.seatnova.theatreservice.controller.dto.SeatResponse;
import com.seatnova.theatreservice.service.ScreenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/screens")
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenService screenService;

    @PostMapping
    public ResponseEntity<ScreenResponse> createScreen(@Valid @RequestBody ScreenRequest request) {
        return new ResponseEntity<>(screenService.createScreen(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScreenResponse> getScreen(@PathVariable UUID id) {
        return ResponseEntity.ok(screenService.getScreen(id));
    }

    @GetMapping("/{screenId}/seats")
    public ResponseEntity<List<SeatResponse>> getSeats(@PathVariable UUID screenId) {
        return ResponseEntity.ok(screenService.getSeats(screenId));
    }

    @PostMapping("/{screenId}/seats")
    public ResponseEntity<List<SeatResponse>> addSeatsToScreen(
            @PathVariable UUID screenId,
            @RequestBody List<SeatRequest> seats) {
        return ResponseEntity.status(HttpStatus.CREATED).body(screenService.addSeats(screenId, seats));
    }

    @GetMapping("/{seatId}/validate")
    public ResponseEntity<?> validateSeat(@PathVariable UUID seatId){
        return ResponseEntity.ok(screenService.validateSeat(seatId));
    }
}
