package edu.modulith.thanhtoan.dto;

public record CreatePaypalOrderResponse(
        Long thanhToanId,
        String orderId,
        String approveUrl
) {
}
