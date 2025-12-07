package edu.modulith.rap.controller;

import edu.modulith.rap.service.GheSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/public/suat-chieu")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class GheSseController {
    private final GheSseService gheSseService;


    @GetMapping("/{lichChieuId}/ghe/stream")
    public SseEmitter stream(@PathVariable Long lichChieuId) {
        return gheSseService.register(lichChieuId);
    }
}
