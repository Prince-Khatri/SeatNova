package com.seatnova.theatreservice.controller;

import com.seatnova.theatreservice.controller.dto.TheatreRequest;
import com.seatnova.theatreservice.controller.dto.TheatreResponse;
import com.seatnova.theatreservice.service.TheatreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/theatres")
@RequiredArgsConstructor
public class TheatreController {

    private final TheatreService theatreService;

    @PostMapping
    public ResponseEntity<TheatreResponse> createTheatre(@Valid @RequestBody TheatreRequest request) {
        return new ResponseEntity<>(theatreService.createTheatre(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TheatreResponse>> getAllTheatres() {
        return ResponseEntity.ok(theatreService.getAllTheatres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheatreResponse> getTheatre(@PathVariable UUID id) {
        return ResponseEntity.ok(theatreService.getTheatre(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TheatreResponse> updateTheatre(@PathVariable UUID id, @Valid @RequestBody TheatreRequest request) {
        return ResponseEntity.ok(theatreService.updateTheatre(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheatre(@PathVariable UUID id) {
        theatreService.deleteTheatre(id);
        return ResponseEntity.noContent().build();
    }
}
