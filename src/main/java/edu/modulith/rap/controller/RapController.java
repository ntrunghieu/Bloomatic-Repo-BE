package edu.modulith.rap.controller;
import edu.modulith.rap.domain.RapRepo;
import edu.modulith.rap.dto.RapDto;
import edu.modulith.rap.dto.TaoRapReq;
import edu.modulith.rap.service.RapService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rap")
@CrossOrigin(origins = "http://localhost:4200")
public class RapController {
  private final RapService rapService;

  // Danh sách rạp (cho admin + client đều xem được)
  @GetMapping
  public List<RapDto> danhSachRap() {
    return rapService.danhSach();
  }

  // Chi tiết rạp
  @GetMapping("/{id}")
  public RapDto chiTiet(@PathVariable("id") Long id) {
    return rapService.chiTiet(id);
  }

  // Tạo rạp (nên để ADMIN mới gọi được trong SecurityConfig)
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public RapDto taoRap(@Valid @RequestBody TaoRapReq req) {
    return rapService.taoRap(req);
  }

  @PutMapping("/{id}")
  public RapDto capNhatRap(
          @PathVariable("id") Long id,
          @Valid @RequestBody TaoRapReq req
  ) {
    return rapService.capNhat(id, req);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void xoaRap(@PathVariable("id") Long id) {
    rapService.xoaRap(id);
  }
}
