package edu.modulith.thanhtoan.dto;

public record PaypalCaptureResult(
        boolean success, String errorMessage
) {
}
