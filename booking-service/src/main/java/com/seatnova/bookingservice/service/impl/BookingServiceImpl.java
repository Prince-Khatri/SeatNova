package com.seatnova.bookingservice.service.impl;

import com.seatnova.bookingservice.dto.BookingRequest;
import com.seatnova.bookingservice.dto.BookingResponse;
import com.seatnova.bookingservice.entity.Booking;
import com.seatnova.bookingservice.entity.BookingSeat;
import com.seatnova.bookingservice.entity.BookingStatus;
import com.seatnova.bookingservice.entity.PaymentStatus;
import com.seatnova.bookingservice.repository.BookingRepository;
import com.seatnova.bookingservice.service.BookingService;
import com.seatnova.bookingservice.service.TheatreValidationService;
import com.seatnova.bookingservice.service.UserValidationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final TheatreValidationService theatreValidationService;
    @Qualifier("userDummyValidationService")
    private final UserValidationService userValidationService;
    @Transactional
    @Override
    public BookingResponse reserveSeats(@Valid BookingRequest request) {
        System.out.println("user validation service:"+this.userValidationService.getClass().getName());


        if(this.userValidationService.validateUserId(request.getUserId())==false)
            throw new EntityNotFoundException("User doesn't exist with userId: "+ request.getUserId());
        Booking booking = new Booking(request);
        booking.setBookingSeats(
                request.getSeatIds()
                        .stream()
                        .map((seatId)->{
                            if(this.theatreValidationService.validateSeatId(seatId)==false) throw new EntityNotFoundException("Seat not found, seatId: "+seatId);
                            BookingSeat bs= new BookingSeat();
                            bs.setBooking(booking);
                            bs.setSeatId(seatId);
                            return bs;
                        })
                        .toList()
        );
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        booking.setBookingStatus(BookingStatus.HOLD);
        booking.setPaymentStatus(PaymentStatus.PENDING);

        return new BookingResponse(this.bookingRepository.save(booking));
    }

    @Override
    public BookingResponse getBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Booking not found with id: " + id));
        return new BookingResponse(booking);

    }
    @Override
    public List<BookingResponse> getUserBooking(UUID userId) {
        return this.bookingRepository.findByUserId(userId)
                .stream()
                .map(booking-> new BookingResponse(booking))
                .toList();
    }
    @Transactional
    @Override
    public BookingResponse confirmBooking(UUID id) {
        Booking booking = this.bookingRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException());

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setPaymentStatus(PaymentStatus.SUCESSFUL);

        return new BookingResponse(booking);
    }
    @Transactional
    @Override
    public BookingResponse releaseBooking(UUID id) {
        Booking booking = this.bookingRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException());

        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.setPaymentStatus(PaymentStatus.ABORTED);


        return new BookingResponse(booking);

    }
    @Transactional
    @Override
    public BookingResponse expireBooking(UUID id) {
        Booking booking = this.bookingRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException());

        booking.setBookingStatus(BookingStatus.EXPIRED);
        booking.setPaymentStatus(PaymentStatus.ABORTED);


        return new BookingResponse(booking);
    }
    @Transactional
    @Override
    public BookingResponse cancelBooking(UUID id) {
        Booking booking = this.bookingRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException());

        if(booking.getBookingStatus()==BookingStatus.CANCELLED){
            throw new IllegalStateException("Booking already Cancelled.");
        }
        if(booking.getBookingStatus()==BookingStatus.EXPIRED){
            throw new IllegalStateException("Booking already Expired.");
        }
        booking.setBookingStatus(BookingStatus.CANCELLED);

        return new BookingResponse(booking);

    }
}
