package edu.modulith.rap.dto;

public record SeatStatusEvent(
        Long lichChieuId,
        Long gheId,
        String nhanGhe,
        String status // "HELD" / "AVAILABLE" / "BOOKED"
) {
}
