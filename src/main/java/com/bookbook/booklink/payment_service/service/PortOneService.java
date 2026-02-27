package com.bookbook.booklink.payment_service.service;

import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.util.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PortOneService {

    private final JsonParser jsonParser;
    private final RestTemplate restTemplate;

    @Value("${portOne.secret}")
    private String apiSecret;

    /**
     * 액세스 토큰 발급
     */
    private String getAccessToken() {
        String url = "https://api.portone.io/login/api-secret";
        Map<String, String> request = Map.of(
                "apiSecret", apiSecret
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getBody() == null || response.getBody().get("accessToken") == null) {
            throw new CustomException(ErrorCode.INVALID_API_TOKEN);
        }


        return (String) response.getBody().get("accessToken");
    }

    /**
     * 결제 정보 조회
     */
    public Integer getPaymentInfo(String paymentId) {
        String token = getAccessToken();
        String url = "https://api.portone.io/v2/payments/" + paymentId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        return jsonParser.extractAmount(response.getBody());
    }

    /**
     * 결제 취소 (환불)
     *
     * @param paymentId 가맹점 주문번호
     * @param amount    환불 금액 (null이면 전체 환불)
     * @param reason    환불 사유
     */
    public void cancelPayment(String paymentId, Integer amount, String reason) {
        String token = getAccessToken();
        String url = "https://api.portone.io/payments/" + paymentId + "/cancel";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        if (amount != null) body.put("amount", amount);
        if (reason != null) body.put("reason", reason);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || responseBody.get("cancellation") == null) {
            throw new CustomException(ErrorCode.PAYMENT_CANCEL_FAILED);
        }

        Map<?, ?> cancellation = (Map<?, ?>) responseBody.get("cancellation");
        String status = (String) cancellation.get("status");
        if (!"SUCCEEDED".equals(status)) {
            throw new CustomException(ErrorCode.PAYMENT_CANCEL_FAILED);
        }

    }

}
