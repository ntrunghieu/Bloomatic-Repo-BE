package edu.modulith.thanhtoan.dto;

public record CapturePaypalRequest(
        Long thanhToanId,
        String orderId
) {
}
