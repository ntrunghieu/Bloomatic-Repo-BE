package edu.modulith.ve.controller;
import edu.modulith.ve.domain.VeRepo;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ve")
public class VeController {
  private final VeRepo repo;
  @GetMapping
  public List<?> list() {
    return repo.findAll();
  }
}
