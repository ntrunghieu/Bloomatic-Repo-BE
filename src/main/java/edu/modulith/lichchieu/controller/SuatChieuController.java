package edu.modulith.lichchieu.controller;

import edu.modulith.common.exception.ResourceNotFoundException;
import edu.modulith.lichchieu.dto.SuatChieuDto;
import edu.modulith.lichchieu.service.LichChieuService;
import edu.modulith.lichchieu.service.SuatChieuService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/suat-chieu")
public class SuatChieuController {

    private final SuatChieuService suatChieuService;

    private final LichChieuService lichChieuService;

    // LẤY DANH SÁCH (GET)
    // URL: /api/suat-chieu?cinemaId=1&roomId=1&date=2025-05-16
    @GetMapping
    public ResponseEntity<List<SuatChieuDto>> getFilteredSlots(
            @RequestParam(required = false) Long cinemaId,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<SuatChieuDto> slots = lichChieuService.findFilteredSlots(cinemaId, roomId, date);
        return ResponseEntity.ok(slots);
    }

    // TẠO MỚI (POST)
    @PostMapping
    public ResponseEntity<SuatChieuDto> createSlot(@RequestBody SuatChieuDto dto) {
        SuatChieuDto newSlot = suatChieuService.createSlot(dto);
        return new ResponseEntity<>(newSlot, HttpStatus.CREATED);
    }

    // CẬP NHẬT (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<SuatChieuDto> updateSlot(
            @PathVariable("id") Long id,
            @RequestBody SuatChieuDto dto) {

        System.out.println("ID Controller nhan duoc: " + dto.id());

        try {
            // 1. Gọi Service để cập nhật và nhận về DTO đã cập nhật
            SuatChieuDto updatedDto = suatChieuService.updateSlot(id, dto);

            // 2. Trả về Status 200 OK và đối tượng đã cập nhật
            return ResponseEntity.ok(updatedDto);
        } catch (ResourceNotFoundException ex) {
            // Xử lý trường hợp không tìm thấy suất chiếu
            return ResponseEntity.notFound().build();
        } catch (DataIntegrityViolationException ex) {
            // Xử lý lỗi ràng buộc dữ liệu (ví dụ: giờ chiếu bị trùng, ID không hợp lệ)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSlot(@PathVariable("id") Long id) {
        try {
            suatChieuService.deleteSlot(id);
            // Trả về Status 204 No Content (thành công nhưng không có nội dung)
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException ex) {
            // Trả về Status 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }
}
