package edu.modulith.theloaiphim.controller;

import edu.modulith.theloaiphim.dto.TheLoaiDto;
import edu.modulith.theloaiphim.dto.TheLoaiPhimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/the-loai")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TheLoaiPhimController {

    private final TheLoaiPhimRepository repo;

    @GetMapping
    public List<TheLoaiDto> getAll() {
        return repo.getAll();
    }
}
