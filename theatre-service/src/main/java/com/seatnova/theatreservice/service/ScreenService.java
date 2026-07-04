package com.seatnova.theatreservice.service;

import com.seatnova.theatreservice.controller.dto.ScreenRequest;
import com.seatnova.theatreservice.controller.dto.ScreenResponse;
import com.seatnova.theatreservice.controller.dto.SeatRequest;
import com.seatnova.theatreservice.controller.dto.SeatResponse;
import com.seatnova.theatreservice.entity.Screen;
import com.seatnova.theatreservice.entity.Seat;
import com.seatnova.theatreservice.entity.Theatre;
import com.seatnova.theatreservice.repository.ScreenRepository;
import com.seatnova.theatreservice.repository.SeatRepository;
import com.seatnova.theatreservice.repository.TheatreRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreenService {
    private final ScreenRepository screenRepository;
    private final TheatreRepository theatreRepository; // In pure microservices, use a Feign Client here
    private final SeatRepository seatRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ScreenResponse createScreen(ScreenRequest request) {
        // Validate theatre exists (Microservice note: Call TheatreService via Feign)
        Theatre theatre = theatreRepository.findById(request.getTheatreId())
                .orElseThrow(() -> new RuntimeException("Theatre not found"));

        Screen screen = modelMapper.map(request, Screen.class);
        screen.setTheatre(theatre);

        return mapToResponse(screenRepository.save(screen));
    }

    @Transactional(readOnly = true)
    public ScreenResponse getScreen(UUID id) {
        return mapToResponse(screenRepository.findById(id).orElseThrow());
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> getSeats(UUID screenId) {
        return seatRepository.findByScreenId(screenId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public List<SeatResponse> addSeats(UUID screenId, List<SeatRequest> requests) {
        // 1. Fetch the Screen (throws exception if not found)
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen not found with id: " + screenId));

        // 2. Fetch existing seat numbers for this screen to prevent duplicates
        List<Seat> existingSeats = seatRepository.findByScreenId(screenId);
        Set<Integer> existingNumbers = existingSeats.stream()
                .map(Seat::getSeatNumber)
                .collect(Collectors.toSet());

        List<Seat> seats = requests.stream()
                .map(request -> modelMapper.map(request, Seat.class))
                .toList();

        // 3. Associate seats with the screen and validate
        for (Seat seat : seats) {
//            if (existingNumbers.contains(seat.getSeatNumber())) {
//                throw new IllegalArgumentException("Seat number " + seat.getSeatNumber() +
//                        " already exists in screen " + screenId);
//            }
            seat.setScreen(screen); // Set the relationship
            existingNumbers.add(seat.getSeatNumber()); // Update set for subsequent checks
        }

        return seatRepository.saveAll(seats).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ScreenResponse mapToResponse(Screen screen) {
        ScreenResponse response = modelMapper.map(screen, ScreenResponse.class);
        response.setTheatreId(screen.getTheatre().getId());
        return response;
    }

    private SeatResponse mapToResponse(Seat seat) {
        SeatResponse response = modelMapper.map(seat, SeatResponse.class);
        response.setScreenId(seat.getScreen().getId());
        return response;
    }
}
