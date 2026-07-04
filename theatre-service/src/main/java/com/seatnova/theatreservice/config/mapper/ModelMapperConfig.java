package com.seatnova.theatreservice.config.mapper;

import com.seatnova.theatreservice.controller.dto.ScreenRequest;
import com.seatnova.theatreservice.controller.dto.SeatRequest;
import com.seatnova.theatreservice.controller.dto.ShowRequest;
import com.seatnova.theatreservice.controller.dto.TheatreRequest;
import com.seatnova.theatreservice.entity.Screen;
import com.seatnova.theatreservice.entity.Seat;
import com.seatnova.theatreservice.entity.Show;
import com.seatnova.theatreservice.entity.Theatre;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        modelMapper.createTypeMap(TheatreRequest.class, Theatre.class)
                .setConverter(context -> {
                    TheatreRequest request = context.getSource();
                    Theatre theatre = context.getDestination() == null ? new Theatre() : context.getDestination();

                    theatre.setName(request.getName());
                    theatre.setCity(request.getCity());
                    theatre.setAddress(request.getAddress());
                    return theatre;
                });

        modelMapper.createTypeMap(ScreenRequest.class, Screen.class)
                .setConverter(context -> {
                    ScreenRequest request = context.getSource();
                    Screen screen = context.getDestination() == null ? new Screen() : context.getDestination();

                    screen.setName(request.getName());
                    screen.setTotalSeats(request.getTotalSeats());
                    return screen;
                });

        modelMapper.createTypeMap(SeatRequest.class, Seat.class)
                .setConverter(context -> {
                    SeatRequest request = context.getSource();
                    Seat seat = context.getDestination() == null ? new Seat() : context.getDestination();

                    seat.setRowLabel(request.getRowLabel());
                    seat.setSeatNumber(request.getSeatNumber());
                    seat.setSeatType(request.getSeatType());
                    return seat;
                });

        modelMapper.createTypeMap(ShowRequest.class, Show.class)
                .setConverter(context -> {
                    ShowRequest request = context.getSource();
                    Show show = context.getDestination() == null ? new Show() : context.getDestination();

                    show.setMovieId(request.getMovieId());
                    show.setStartTime(request.getStartTime());
                    show.setEndTime(request.getEndTime());
                    show.setLanguage(request.getLanguage());
                    show.setBasePrice(request.getBasePrice());
                    return show;
                });

        return modelMapper;
    }
}
