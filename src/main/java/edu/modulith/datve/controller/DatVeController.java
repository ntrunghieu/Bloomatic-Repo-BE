package edu.modulith.datve.controller;
import edu.modulith.datve.domain.DatVeRepo;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dat-ve")
public class DatVeController {
  private final DatVeRepo repo;

  @GetMapping
  public List<?> list() {
    return repo.findAll();
  }
}
