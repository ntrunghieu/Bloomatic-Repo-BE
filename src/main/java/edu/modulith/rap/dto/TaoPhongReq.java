package edu.modulith.rap.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record TaoPhongReq(
        @NotBlank(message = "Tên phòng chiếu không được để trống")
        String tenPhong,

        @NotBlank(message = "Loại phòng chiếu không được để trống")
        String loaiPhong,

        @Min(value = 1) @Max(26)
        int hang,

        @Min(value = 1) @Max(40)
        int cot
) {}

