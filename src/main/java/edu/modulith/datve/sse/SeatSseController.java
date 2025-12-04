package edu.modulith.datve.sse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SeatSseController {
  @GetMapping(value = "/api/seat-events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribe() {
    SseEmitter emitter = new SseEmitter(0L);
    // TODO: wire real events for seat hold/release
    return emitter;
  }
}
