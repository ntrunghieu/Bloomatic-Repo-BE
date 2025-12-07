package edu.modulith.rap.controller;

import edu.modulith.auth.context.AuthenticationFacade;
import edu.modulith.rap.domain.Ghe;
import edu.modulith.rap.domain.GheRepo;
import edu.modulith.rap.dto.SeatStatusEvent;
import edu.modulith.rap.service.GheSseService;
import edu.modulith.rap.service.GiuGheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/suat-chieu")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class GiuGheController {
    private final GiuGheService giuGheService;
    private final GheSseService gheSseService;
    private final GheRepo gheRepo;
    private final AuthenticationFacade auth;

    public record HoldRequest(List<Long> seatIds) {}
    public record HoldResponse(boolean success, String message) {}

    @PostMapping("/{lichChieuId}/hold")
    public ResponseEntity<HoldResponse> holdSeats(
            @PathVariable Long lichChieuId,
            @RequestBody HoldRequest req
    ) {
        Long userId = auth.getCurrentUserId();
        System.out.println("null ở đây" + userId);
        boolean ok = giuGheService.tryHoldSeats(userId, lichChieuId, req.seatIds());
        if (!ok) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new HoldResponse(false, "Một số ghế đã bị giữ bởi người khác"));
        }

        // broadcast SSE
        List<Ghe> gheList = gheRepo.findAllById(req.seatIds());
        gheList.forEach(g -> gheSseService.sendEvent(
                new SeatStatusEvent(lichChieuId, g.getId(), g.getNhanGhe(), "HELD")
        ));

        return ResponseEntity.ok(new HoldResponse(true, "Giữ ghế thành công"));
    }

    @PostMapping("/{lichChieuId}/release")
    public ResponseEntity<Void> releaseSeats(
            @PathVariable Long lichChieuId,
            @RequestBody HoldRequest req
    ) {
        Long userId = auth.getCurrentUserId();
        giuGheService.releaseSeats(userId, lichChieuId, req.seatIds());

        List<Ghe> gheList = gheRepo.findAllById(req.seatIds());
        gheList.forEach(g -> gheSseService.sendEvent(
                new SeatStatusEvent(lichChieuId, g.getId(), g.getNhanGhe(), "AVAILABLE")
        ));

        return ResponseEntity.ok().build();
    }
}
