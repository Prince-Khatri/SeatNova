package com.seatnova.bookingservice.service.impl;

import com.seatnova.bookingservice.entity.SeatStatus;
import com.seatnova.bookingservice.service.SeatLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatLockServiceImpl implements SeatLockService {
    private final StringRedisTemplate redisTemplate;

    String getKey(UUID showId, UUID  seatId){
        return showId.toString() + ":" + seatId.toString();
    }

    @Override
    public boolean lockSeats(UUID showId, List<UUID> seatIds) {
        boolean flag = false;
        for(UUID seatId:seatIds){
            String status = redisTemplate.opsForValue().get(getKey(showId, seatId));
            // seat exist in redis
            if (status == null) {
                throw new IllegalStateException(
                        "Seat not initialized in Redis : " + seatId
                );
            }
            // seat available
            if(!status.equals("AVAILABLE")) return false;
        }
        for(UUID seatId:seatIds){
            redisTemplate
                    .opsForValue() // get opearations for key/value
                    .getAndSet(
                            getKey(showId, seatId), // key
                            SeatStatus.HOLD.name() // value
                    );
        }
        return true;
    }

    @Override
    public void releaseSeats(UUID showId, List<UUID> seatIds) {

        for(UUID seatId:seatIds){
            redisTemplate.opsForValue().getAndSet(
                    getKey(showId, seatId),
                    SeatStatus.AVAILABLE.name()
            );
        }
    }

    @Override
    public void confirmSeats(UUID showId, List<UUID> seatIds) {
        for(UUID seatId:seatIds){
            redisTemplate.opsForValue().getAndSet(
                    getKey(showId, seatId),
                    SeatStatus.BOOKED.name()
            );
        }
    }
}