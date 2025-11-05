package com.notification.service;

import com.notification.sse.SseEmitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 10 * 60 * 1000L;
    private final SseEmitters sseEmitters;

    public SseEmitter subscribe(Long userId, Long auctionId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        sseEmitters.add(userId, auctionId, emitter);

        // front useEffect에서 마무리로 정리해줘야 함
        emitter.onCompletion(() -> sseEmitters.remove(userId, auctionId));
        emitter.onTimeout(() -> sseEmitters.remove(userId, auctionId));

        // 처음 연결 시 확인차 보내기
        sendToClient(emitter, "connect", "Connection successful for auction " + auctionId);
        return emitter;
    }

    public void sendPriceUpdateToAll(Long auctionId, long newPrice) {
        sseEmitters.getAuctionEmitters(auctionId).forEach(emitter -> {
            sendToClient(emitter, "price-update", Map.of("price", newPrice));
        });
    }

    public void sendNotificationToUser(Long userId, Long auctionId, String eventName, Object data) {
        sseEmitters.getUserEmitter(userId, auctionId).ifPresent(emitter -> {
            sendToClient(emitter, eventName, data);
        });
    }

    private void sendToClient(SseEmitter emitter, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
        } catch (IOException e) {
            log.error("SSE error", e);
            emitter.completeWithError(e);
        }
    }

}
