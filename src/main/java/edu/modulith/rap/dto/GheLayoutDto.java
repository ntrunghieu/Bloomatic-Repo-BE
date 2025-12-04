package edu.modulith.rap.dto;

import java.util.List;

public record GheLayoutDto(
        int hang,
        int cot,
        List<GheDto> gheDTO
) {
}
