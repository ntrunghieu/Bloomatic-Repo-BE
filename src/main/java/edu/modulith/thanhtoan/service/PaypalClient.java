package edu.modulith.thanhtoan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class PaypalClient {

    private final RestTemplate restTemplate;
    public record PaypalCreateOrderResult(String orderId, String approveUrl) {}
    public record PaypalCaptureResult(boolean success, String errorMessage) {}

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.base-url:https://api-m.sandbox.paypal.com}")
    private String baseUrl;


    // Phương thức này CẦN trả về String (AccessToken) và cần xử lý ngoại lệ
    private String getAccessToken() throws Exception {
        String url = baseUrl + "/v1/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new IllegalStateException(
                        "Không lấy được access token từ PayPal. Status = " + response.getStatusCode());
            }

            Object token = response.getBody().get("access_token");
            if (token == null) {
                throw new IllegalStateException("Phản hồi PayPal không có access_token");
            }

            return token.toString();
        } catch (RestClientException ex) {
            throw new RuntimeException("Lỗi gọi PayPal /v1/oauth2/token", ex);
        }
    }

    // Cần thêm xử lý ngoại lệ
    public PaypalCreateOrderResult createOrder(BigDecimal amount, String currency, String invoiceId) throws Exception {
        String accessToken = getAccessToken();

        String url = baseUrl + "/v2/checkout/orders";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Đảm bảo amount có 2 chữ số thập phân
        String amountStr = amount.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();

        // amount object
        Map<String, Object> amountObj = new HashMap<>();
        amountObj.put("currency_code", currency); // "USD"
        amountObj.put("value", amountStr);        // "10.50"

        // purchase_unit
        Map<String, Object> purchaseUnit = new HashMap<>();
        purchaseUnit.put("amount", amountObj);
        purchaseUnit.put("invoice_id", invoiceId);

        // application_context: nếu bạn dùng redirect thì cần return_url/cancel_url
        // còn nếu dùng JS SDK Buttons (createOrder -> orderId) thì có thể bỏ
        Map<String, Object> appContext = new HashMap<>();
        appContext.put("brand_name", "Bloomatic Cinema");
        appContext.put("landing_page", "NO_PREFERENCE");
        appContext.put("user_action", "PAY_NOW");
        appContext.put("shipping_preference", "NO_SHIPPING");
        // Nếu bạn có URL web thật:
        // appContext.put("return_url", "https://your-frontend/payment/success");
        // appContext.put("cancel_url", "https://your-frontend/payment/cancel");

        Map<String, Object> payload = new HashMap<>();
        payload.put("intent", "CAPTURE");
        payload.put("purchase_units", List.of(purchaseUnit));
        payload.put("application_context", appContext);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCodeValue() != 201 || response.getBody() == null) {
                throw new IllegalStateException(
                        "Tạo PayPal order thất bại. Status = " + response.getStatusCode());
            }

            Map<String, Object> body = response.getBody();
            String orderId = (String) body.get("id");

            String approveUrl = null;
            Object linksObj = body.get("links");
            if (linksObj instanceof List<?> links) {
                for (Object linkObj : links) {
                    if (linkObj instanceof Map<?,?> m) {
                        Object rel = m.get("rel");
                        if ("approve".equals(rel)) {
                            approveUrl = (String) m.get("href");
                            break;
                        }
                    }
                }
            }

            if (orderId == null) {
                throw new IllegalStateException("Phản hồi PayPal không có id (orderId)");
            }

            return new PaypalCreateOrderResult(orderId, approveUrl);
        } catch (RestClientException ex) {
            throw new RuntimeException("Lỗi gọi PayPal /v2/checkout/orders", ex);
        }
    }

    // Cần thêm xử lý ngoại lệ
    public PaypalCaptureResult captureOrder(String orderId) throws Exception {
        String accessToken = getAccessToken();

        String url = baseUrl + "/v2/checkout/orders/" + orderId + "/capture";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Body có thể là {} theo spec
        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(Collections.emptyMap(), headers);

        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return new PaypalCaptureResult(false,
                        "Capture thất bại. Status = " + response.getStatusCode());
            }

            Map<String, Object> body = response.getBody();
            String status = (String) body.get("status"); // Thường là "COMPLETED"

            if ("COMPLETED".equalsIgnoreCase(status)) {
                return new PaypalCaptureResult(true, null);
            } else {
                return new PaypalCaptureResult(false,
                        "Trạng thái PayPal không thành công: " + status);
            }
        } catch (RestClientException ex) {
            return new PaypalCaptureResult(false,
                    "Lỗi gọi PayPal capture: " + ex.getMessage());
        }
    }
}
