package edu.modulith.rap.dto;

import jakarta.validation.constraints.NotBlank;

public record TaoRapReq(
        @NotBlank(message = "Tên rạp chiếu không được để trống")
        String tenRap,

        @NotBlank(message = "Địa chỉ không được để trống")
        String diaChi,

        String dienThoai,
        String email
) {}
