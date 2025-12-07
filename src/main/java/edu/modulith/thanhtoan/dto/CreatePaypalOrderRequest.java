package edu.modulith.thanhtoan.dto;

import java.math.BigDecimal;
import java.util.List;

public record CreatePaypalOrderRequest(
        Long lichChieuId,
        List<Long> seatIds,
        List<String> seatCodes,
        BigDecimal clientAmount
) {
}
