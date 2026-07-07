package com.seatnova.theatreservice.service;

import com.seatnova.theatreservice.controller.dto.ShowRequest;
import com.seatnova.theatreservice.controller.dto.ShowResponse;
import com.seatnova.theatreservice.entity.Screen;
import com.seatnova.theatreservice.entity.Show;
import com.seatnova.theatreservice.entity.ShowStatus;
import com.seatnova.theatreservice.repository.ScreenRepository;
import com.seatnova.theatreservice.repository.SeatRepository;
import com.seatnova.theatreservice.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShowService {
    private final ShowRepository showRepository;
    private final ScreenRepository screenRepository;
    private final ModelMapper modelMapper;
    private final MovieValidationService movieValidationService;
    private final SeatRepository seatRepository;

    @Transactional
    public ShowResponse createShow(ShowRequest request) {
        movieValidationService.validateMovieExists(request.getMovieId());

        Screen screen = screenRepository.findById(request.getScreenId())
                .orElseThrow(() -> new RuntimeException("Screen not found with id: " + request.getScreenId()));

        List<Show> existingShows = showRepository.findByScreenId(request.getScreenId());

        boolean isOverlapping = existingShows.stream().anyMatch(s ->
                request.getStartTime().isBefore(s.getEndTime()) &&
                        request.getEndTime().isAfter(s.getStartTime())
        );

        if (isOverlapping) {
            throw new RuntimeException("Show timings overlap with an existing show.");
        }

        Show show = modelMapper.map(request, Show.class);
        show.setScreen(screen);
        show.setStatus(ShowStatus.ACTIVE);

        return mapToResponse(showRepository.save(show));
    }

    @Transactional
    public void cancelShow(UUID showId) {
        Show show = showRepository.findById(showId).orElseThrow();
        show.setStatus(ShowStatus.CANCELLED);
        showRepository.save(show);
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> getShowsByMovie(UUID movieId) {
        try {
            movieValidationService.validateMovieExists(movieId);
        }
        catch (Exception e){
            System.out.println("Movie Doesn't exist");
            return null;
        }

        return showRepository.findByMovieId(movieId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> getShowsByCity(String city) {
        return showRepository.findByScreen_Theatre_City(city).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ShowResponse getShow(UUID id) {
        return mapToResponse(showRepository.findById(id).orElseThrow());
    }
    public List<ShowResponse> getAllShows() {
        return showRepository.findAll().stream()
                .map(this :: mapToResponse)
                .toList();
    }
    private ShowResponse mapToResponse(Show show) {
        ShowResponse response = modelMapper.map(show, ShowResponse.class);
        response.setScreenId(show.getScreen().getId());
        return response;
    }

    public Boolean validateShow(UUID id) {
        return seatRepository.existsById(id);
    }
}
