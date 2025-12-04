package edu.modulith.rap.controller;

// DataLookupController.java
import edu.modulith.phim.dto.PhimFilterDto;
import edu.modulith.rap.dto.PhongFilterDto;
import edu.modulith.rap.dto.RapFilterDto;
import edu.modulith.rap.service.DataLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/filter")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class DataLookupController {

    private final DataLookupService dataLookupService;

    @GetMapping("/rap")
    public List<RapFilterDto> getCinemas() {
        return dataLookupService.getAllCinemas();
    }

    @GetMapping("/phong")
    public List<PhongFilterDto> getRooms() {
        return dataLookupService.getAllRooms();
    }

    @GetMapping("/phim")
    public List<PhimFilterDto> getMovies() {
        return dataLookupService.getAllMovies();
    }
}
