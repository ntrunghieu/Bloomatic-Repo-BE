package edu.modulith.rap.service;

import edu.modulith.phim.domain.Phim;
import edu.modulith.phim.domain.PhimRepo;
import edu.modulith.phim.dto.PhimFilterDto;
import edu.modulith.rap.domain.Phong;
import edu.modulith.rap.domain.PhongRepo;
import edu.modulith.rap.domain.Rap;
import edu.modulith.rap.domain.RapRepo;
import edu.modulith.rap.dto.PhongFilterDto;
import edu.modulith.rap.dto.RapFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataLookupService {

    private final RapRepo rapRepo;
    private final PhongRepo phongRepo;
    private final PhimRepo phimRepo;

    public List<RapFilterDto> getAllCinemas() {
        return rapRepo.findAll().stream()
                .map(RapFilterDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PhongFilterDto> getAllRooms() {
        return phongRepo.findAll().stream()
                .map(PhongFilterDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PhimFilterDto> getAllMovies() {
        return phimRepo.findAll().stream()
                .map(PhimFilterDto::fromEntity)
                .collect(Collectors.toList());
    }


}
