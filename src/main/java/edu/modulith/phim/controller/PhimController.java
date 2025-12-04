package edu.modulith.phim.controller;
import edu.modulith.phim.dto.PhimDto;
import edu.modulith.phim.dto.PhimOption;
import edu.modulith.phim.dto.PhimRequest;
import edu.modulith.phim.service.PhimService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/phim")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PhimController {

  private final PhimService phimService;

  @GetMapping
  public Page<PhimDto> timKiemPhim(
          @RequestParam(value = "trangThai", required = false) String trangThai,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "12") int size
  ) {
    return phimService.timKiemPhim(trangThai, page, size);
  }

  @GetMapping("/available")
  public ResponseEntity<List<PhimOption>> getAvailableMovies() {
    List<PhimOption> movies = phimService.findAvailableMovies();
    return ResponseEntity.ok(movies);
  }

  @GetMapping("/{id}")
  public PhimDto chiTietPhim(@PathVariable Long id) {
    return phimService.chiTietPhim(id);
  }

  @PostMapping("/admin")
  public PhimDto themPhim(@RequestBody PhimRequest request) {
    return phimService.themPhim(request);
  }

  @PutMapping("/admin/{id}")
  public PhimDto capNhatPhim(
          @PathVariable Long id,
          @RequestBody PhimRequest request
  ) {
    return phimService.capNhatPhim(id, request);
  }
}

