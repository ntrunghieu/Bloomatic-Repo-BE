package edu.modulith.rap.controller;

import edu.modulith.rap.dto.GheLayoutUserDto;
import edu.modulith.rap.service.GheLayoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/suat-chieu")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class GheLayoutController {
    private final GheLayoutService gheLayoutService;

    @GetMapping("/{lichChieuId}/ghe")
    public GheLayoutUserDto getSeatLayout(@PathVariable Long lichChieuId) {
        return gheLayoutService.buildLayoutForShowtime(lichChieuId);
    }
}
