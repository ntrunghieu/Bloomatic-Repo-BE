package edu.modulith.thanhtoan.dto;

public record PaypalCreateOrderResult(
        String orderId, String approveUrl
) {
}
