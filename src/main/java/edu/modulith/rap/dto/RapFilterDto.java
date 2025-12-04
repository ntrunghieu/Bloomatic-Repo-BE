package edu.modulith.rap.dto;

import edu.modulith.rap.domain.Rap;

public record RapFilterDto(
        Long id, String tenRap
) {
    public static RapFilterDto fromEntity(Rap rap) {
        return new RapFilterDto(rap.getId(), rap.getTenRap());
    }
}
