package edu.modulith.rap.controller;

import edu.modulith.rap.service.TrangThaiGheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ghe/trang-thai")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class TrangThaiGheController {
    private final TrangThaiGheService trangThaiGheService;

    @GetMapping
    public ResponseEntity<Map<Long, Boolean>> getRoomsConfigStatus() {
        Map<Long, Boolean> statusMap = trangThaiGheService.getRoomsConfigStatus();
        return ResponseEntity.ok(statusMap);
    }
}
