package edu.modulith.lichchieu.controller;
import edu.modulith.lichchieu.domain.LichChieuRepo;
import edu.modulith.lichchieu.dto.LichChieuDto;
import edu.modulith.lichchieu.service.LichChieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lich-chieu")
@CrossOrigin(origins = "http://localhost:4200")
public class LichChieuController {

  private final LichChieuRepo repo;

  private final LichChieuService lichChieuService;

  @GetMapping
  public ResponseEntity<List<LichChieuDto>> getAllShowtimes() {
    List<LichChieuDto> showtimes = lichChieuService.findAllShowtimes();
    return ResponseEntity.ok(showtimes);
  }

  @PostMapping
  public ResponseEntity<LichChieuDto> createShowtime(@RequestBody LichChieuDto dto) {
    LichChieuDto createdDto = lichChieuService.createShowtime(dto);
    return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
  }

  /**
   * Cập nhật (PUT /api/admin/showtimes/{id})
   */
  @PutMapping("/{id}")
  public ResponseEntity<LichChieuDto> updateShowtime(@PathVariable Long id, @RequestBody LichChieuDto dto) {
    LichChieuDto updatedDto = lichChieuService.updateShowtime(id, dto);
    return ResponseEntity.ok(updatedDto);
  }

  /**
   * Xóa (DELETE /api/admin/showtimes/{id})
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteShowtime(@PathVariable Long id) {
    lichChieuService.deleteShowtime(id);
    return ResponseEntity.noContent().build();
  }


}
