package com.notification.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitters {

    // Map<AuctionId, Map<UserId, SseEmitter>>
    private final Map<Long, Map<Long, SseEmitter>> auctionEmitters = new ConcurrentHashMap<>();

    public void add(Long userId, Long auctionId, SseEmitter emitter) {
        Map<Long, SseEmitter> userEmitters = auctionEmitters.computeIfAbsent(auctionId, key -> new ConcurrentHashMap<>());
        userEmitters.put(userId, emitter);
    }

    public void remove(Long userId, Long auctionId) {
        Map<Long, SseEmitter> userEmitters = auctionEmitters.get(auctionId);

        if (userEmitters != null) {
            userEmitters.remove(userId);
            if (userEmitters.isEmpty()) {
                auctionEmitters.remove(auctionId);
            }
        }
    }

    public Optional<SseEmitter> getUserEmitter(Long userId, Long auctionId) {
        Map<Long, SseEmitter> userEmitters = auctionEmitters.get(auctionId);
        if (userEmitters != null) {
            return Optional.ofNullable(userEmitters.get(userId));
        }
        return Optional.empty();
    }

    public Collection<SseEmitter> getAuctionEmitters(Long auctionId) {
        Map<Long, SseEmitter> userEmitters = auctionEmitters.get(auctionId);
        if (userEmitters != null) {
            return userEmitters.values();
        }
        return Collections.emptyList();
    }

}
