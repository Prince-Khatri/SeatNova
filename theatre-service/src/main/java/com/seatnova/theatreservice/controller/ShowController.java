package com.seatnova.theatreservice.controller;

import com.seatnova.theatreservice.controller.dto.ShowRequest;
import com.seatnova.theatreservice.controller.dto.ShowResponse;
import com.seatnova.theatreservice.entity.Show;
import com.seatnova.theatreservice.service.ShowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @PostMapping
    public ResponseEntity<ShowResponse> createShow(@Valid @RequestBody ShowRequest request) {
        return new ResponseEntity<>(showService.createShow(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowResponse> getShow(@PathVariable UUID id) {
        return ResponseEntity.ok(showService.getShow(id));
    }

    @GetMapping("/{id}/validate")
    public ResponseEntity<?> validateShow(@PathVariable UUID id){
        return ResponseEntity.ok(showService.validateShow(id));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowResponse>> getShowsByMovie(@PathVariable UUID movieId) {
        return ResponseEntity.ok(showService.getShowsByMovie(movieId));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<ShowResponse>> getShowsByCity(@PathVariable String city) {
        return ResponseEntity.ok(showService.getShowsByCity(city));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelShow(@PathVariable UUID id) {
        showService.cancelShow(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ShowResponse>> getAllShows() {
        return ResponseEntity.ok(showService.getAllShows());
    }
}
