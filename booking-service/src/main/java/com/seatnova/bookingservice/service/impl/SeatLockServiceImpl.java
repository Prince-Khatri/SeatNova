package com.seatnova.bookingservice.service.impl;

import com.seatnova.bookingservice.entity.SeatStatus;
import com.seatnova.bookingservice.service.SeatLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatLockServiceImpl implements SeatLockService {
    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> holdSeatsScript;

    String getKey(UUID showId, UUID  seatId){
        return showId.toString() + ":" + seatId.toString();
    }

    @Override
    public boolean lockSeats(UUID showId, List<UUID> seatIds) {
        List<String> keys = seatIds
                .stream()
                .map((seatId)->{
                    return getKey(showId, seatId);
                })
                .toList();

        Long result = redisTemplate.execute(
                holdSeatsScript,
                keys
        );
        return result != null && result == 1;
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