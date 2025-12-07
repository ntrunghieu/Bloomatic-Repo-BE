package edu.modulith.rap.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GiuGheService {
//    private static final Duration HOLD_TTL = Duration.ofSeconds(180);

    private final StringRedisTemplate redisTemplate;
    private static final long HOLD_MILLIS = 180_000L;
    private final Map<String, Long> holderBySeat = new ConcurrentHashMap<>();
    private final Map<String, Long> expireAtBySeat = new ConcurrentHashMap<>();


    private String key(Long lichChieuId, Long gheId) {
        return "hold:lichchieu:" + lichChieuId + ":ghe:" + gheId;
    }


    public boolean tryHoldSeats(Long userId, Long lichChieuId, List<Long> seatIds) {
        long now = System.currentTimeMillis();
        long expireAt = now + HOLD_MILLIS;

        for (Long gheId : seatIds) {
            String k = key(lichChieuId, gheId);
            Long holder = holderBySeat.get(k);
            Long expireTime = expireAtBySeat.get(k);

            // nếu đã hết hạn thì coi như trống
            if (expireTime != null && expireTime < now) {
                holder = null;
            }

            if (holder != null && !holder.equals(userId)) {
                return false; // đã có người khác giữ
            }
        }

        for (Long gheId : seatIds) {
            String k = key(lichChieuId, gheId);
            holderBySeat.put(k, userId);
            expireAtBySeat.put(k, expireAt);
        }
        return true;
    }

    public void releaseSeats(Long userId, Long lichChieuId, List<Long> seatIds) {
        long now = System.currentTimeMillis();
        for (Long gheId : seatIds) {
            String k = key(lichChieuId, gheId);
            Long holder = holderBySeat.get(k);
            if (holder != null && holder.equals(userId)) {
                holderBySeat.remove(k);
                expireAtBySeat.remove(k);
            }
        }
    }

    public boolean isHeld(Long lichChieuId, Long gheId) {
        long now = System.currentTimeMillis();
        String k = key(lichChieuId, gheId);
        Long expireTime = expireAtBySeat.get(k);
        if (expireTime == null || expireTime < now) {
            holderBySeat.remove(k);
            expireAtBySeat.remove(k);
            return false;
        }
        return true;
    }
//    public boolean tryHoldSeats(Long userId, Long lichChieuId, List<Long> seatIds) {
//        // check conflict
//        for (Long gheId : seatIds) {
//            String k = key(lichChieuId, gheId);
//            String existing = redisTemplate.opsForValue().get(k);
//            if (existing != null && !existing.equals(userId.toString())) {
//                // ghế đã bị người khác giữ
//                return false;
//            }
//        }
//        // set / gia hạn TTL
//        for (Long gheId : seatIds) {
//            String k = key(lichChieuId, gheId);
//            redisTemplate.opsForValue().set(k, userId.toString(), HOLD_TTL);
//        }
//        return true;
//    }
//
//    public void releaseSeats(Long userId, Long lichChieuId, List<Long> seatIds) {
//        for (Long gheId : seatIds) {
//            String k = key(lichChieuId, gheId);
//            String val = redisTemplate.opsForValue().get(k);
//            if (val != null && val.equals(userId.toString())) {
//                redisTemplate.delete(k);
//            }
//        }
//    }
//
//    public boolean isHeld(Long lichChieuId, Long gheId) {
//        String k = key(lichChieuId, gheId);
//        return redisTemplate.hasKey(k);
//    }
//
//    public String getHolder(Long lichChieuId, Long gheId) {
//        return redisTemplate.opsForValue().get(key(lichChieuId, gheId));
//    }
}
