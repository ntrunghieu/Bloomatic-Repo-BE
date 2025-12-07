package edu.modulith.rap.dto;

import java.util.List;

public record GheLayoutUserDto (
        int hang,
        int cot,
        List<GheUserDto> gheUserDto
){

}
