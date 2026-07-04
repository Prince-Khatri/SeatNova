package com.seatnova.theatreservice.service;

import com.seatnova.theatreservice.controller.dto.TheatreRequest;
import com.seatnova.theatreservice.controller.dto.TheatreResponse;
import com.seatnova.theatreservice.entity.Theatre;
import com.seatnova.theatreservice.repository.TheatreRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TheatreService {

    private final TheatreRepository theatreRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public TheatreResponse createTheatre(TheatreRequest request) {
        Theatre theatre = modelMapper.map(request, Theatre.class);
        return mapToResponse(theatreRepository.save(theatre));
    }

    @Transactional(readOnly = true)
    public TheatreResponse getTheatre(UUID id) {
        return mapToResponse(theatreRepository.findById(id).orElseThrow());
    }

    @Transactional(readOnly = true)
    public List<TheatreResponse> getAllTheatres() {
        return theatreRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteTheatre(UUID id) {
        theatreRepository.deleteById(id);
    }

    @Transactional
    public TheatreResponse updateTheatre(UUID id, TheatreRequest request) {
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theatre not found with id: " + id));

        modelMapper.map(request, theatre);

        return mapToResponse(theatreRepository.save(theatre));
    }

    private TheatreResponse mapToResponse(Theatre theatre) {
        return modelMapper.map(theatre, TheatreResponse.class);
    }
}
