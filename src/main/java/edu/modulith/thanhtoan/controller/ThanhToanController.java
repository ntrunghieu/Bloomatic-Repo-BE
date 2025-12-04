package edu.modulith.thanhtoan.controller;
import edu.modulith.thanhtoan.domain.ThanhToanRepo;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/thanh-toan")
public class ThanhToanController {
  private final ThanhToanRepo repo;
  @GetMapping
  public List<?> list() {
    return repo.findAll();
  }
}
