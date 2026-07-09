package com.seatnova.bookingservice.service.impl;

import com.seatnova.bookingservice.config.RabbitMQConfig;
import com.seatnova.bookingservice.dto.BookingRequest;
import com.seatnova.bookingservice.dto.BookingResponse;
import com.seatnova.bookingservice.entity.Booking;
import com.seatnova.bookingservice.entity.BookingSeat;
import com.seatnova.bookingservice.entity.BookingStatus;
import com.seatnova.bookingservice.entity.PaymentStatus;
import com.seatnova.bookingservice.repository.BookingRepository;
import com.seatnova.bookingservice.service.BookingService;
import com.seatnova.bookingservice.service.SeatLockService;
import com.seatnova.bookingservice.service.TheatreValidationService;
import com.seatnova.bookingservice.service.UserValidationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
//    private final TheatreValidationService theatreValidationService;
    private final SeatLockService seatLockService;
    @Qualifier("userDummyValidationService")
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;


    @Transactional
    @Override
    public BookingResponse reserveSeats(@Valid BookingRequest request) {
        log.info("user validation service:"+this.userValidationService.getClass().getName());

        // validate user id
        if(this.userValidationService.validateUserId(request.getUserId())==false)
            throw new EntityNotFoundException("User doesn't exist with userId: "+ request.getUserId());
        // validate seatId
        boolean locked = seatLockService.lockSeats(
                request.getShowId(),
                request.getSeatIds()
        );
        if(!locked) {
            throw new IllegalStateException(
                    "One or more seats are not available"
            );
        }
        Booking booking = new Booking(request);

        booking.setBookingSeats(
            request
                .getSeatIds()
                .stream()
                .map((seatId)->{
                    BookingSeat bs = new BookingSeat();
                    bs.setSeatId(seatId);
                    bs.setBooking(booking);
                    return bs;
                })
                .toList()

        );

        booking.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        booking.setBookingStatus(BookingStatus.HOLD);
        booking.setPaymentStatus(PaymentStatus.PENDING);

        Booking savedBooking=null;
        // release seats if they are not saved.
        try{
            savedBooking=this.bookingRepository.save(booking);
        } catch (Exception e) {
            seatLockService.releaseSeats(request.getShowId(),request.getSeatIds());
        }
        try{
            Map<String, Object> payload = new HashMap<>();
            payload.put("bookingId", savedBooking.getId());
            payload.put("userId", savedBooking.getUserId());
            payload.put("totalAmount", savedBooking.getTotalAmount());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.BOOKING_EXCHANGE,
                    RabbitMQConfig.BOOKING_CREATED_KEY,
                    payload
            );

        } catch (Exception e) {
            log.warn("Async notification warning: Broker unreachable. " + e.getMessage());
        }

        return new BookingResponse(savedBooking);
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

        if (booking.getBookingStatus() != BookingStatus.HOLD) {
            throw new IllegalStateException(
                    "Booking cannot be confirmed."
            );
        }
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setPaymentStatus(PaymentStatus.SUCESSFUL);
        seatLockService.confirmSeats(
                booking.getShowId(),
                booking.getBookingSeats()
                    .stream()
                    .map((bs)-> bs.getSeatId())
                    .toList()
        );
        return new BookingResponse(booking);
    }
    @Transactional
    @Override
    public BookingResponse releaseBooking(UUID id) {
        Booking booking = this.bookingRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException());
        // checking if the seat is available to release
        if (booking.getBookingStatus() != BookingStatus.HOLD) {
            throw new IllegalStateException(
                    "Booking cannot be confirmed."
            );
        }
        booking.setBookingStatus(BookingStatus.EXPIRED);
        booking.setPaymentStatus(PaymentStatus.ABORTED);

        seatLockService.releaseSeats(
                booking.getShowId(),
                booking.getBookingSeats()
                        .stream()
                        .map((bs)->{
                                    return bs.getSeatId();
                                }
                        ).toList()
        );

        return new BookingResponse(booking);
    }
    @Transactional
    @Override
    public BookingResponse expireBooking(UUID id) {
        return releaseBooking(id);
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

        seatLockService.releaseSeats(
                booking.getShowId(),
                booking.getBookingSeats()
                        .stream()
                        .map((bs)->bs.getSeatId())
                        .toList()
        );

        try{
            Map<String, Object> payload = new HashMap<>();
            payload.put("bookingId", booking.getId());
            payload.put("userId", booking.getUserId());
            payload.put("refundAmount", booking.getTotalAmount());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.BOOKING_EXCHANGE,
                    RabbitMQConfig.BOOKING_CANCELLED_KEY,
                    payload
            );
        }
        catch (Exception e){
            System.err.println("Async notification warning: Broker unreachable. " + e.getMessage());
        }

        return new BookingResponse(booking);

    }
}
