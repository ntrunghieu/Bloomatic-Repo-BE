package edu.modulith.rap.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.modulith.rap.dto.SeatStatusEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class GheSseService {
    private final Map<Long, List<SseEmitter>> emittersByShowtime = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SseEmitter register(Long lichChieuId) {
        SseEmitter emitter = new SseEmitter(0L); // không timeout
        emittersByShowtime.computeIfAbsent(lichChieuId, id -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> remove(lichChieuId, emitter));
        emitter.onTimeout(() -> remove(lichChieuId, emitter));
        emitter.onError(e -> remove(lichChieuId, emitter));

        return emitter;
    }

    private void remove(Long lichChieuId, SseEmitter emitter) {
        List<SseEmitter> list = emittersByShowtime.get(lichChieuId);
        if (list != null) {
            list.remove(emitter);
        }
    }

    public void sendEvent(SeatStatusEvent event) {
        List<SseEmitter> list = emittersByShowtime.getOrDefault(event.lichChieuId(), List.of());
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            return;
        }

        for (SseEmitter emitter : list) {
            try {
                emitter.send(SseEmitter.event()
                        .name("seat-status")
                        .data(json));
            } catch (Exception ex) {
                emitter.complete();
            }
        }
    }
}
