package com.bookbook.booklink.common.util;

import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 결제 응답 JSON을 파싱하는 유틸리티 서비스 클래스입니다.
 */
@Slf4j
@Service
public class JsonParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * JSON 문자열에서 결제 금액(total)을 추출합니다.
     *
     * @param rawResponse TOSS Payments API에서 반환된 원시 JSON 응답 문자열
     * @return 추출된 결제 금액
     */
    public int extractAmount(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);

            // 'payment' 노드 확인
            JsonNode payment = root.path("payment");
            if (payment.isMissingNode()) {
                throw new CustomException(ErrorCode.JSON_PARSING_ERROR);
            }

            // 'transactions' 배열 확인
            JsonNode transactions = payment.path("transactions");
            if (!transactions.isArray() || transactions.size() == 0) {
                throw new CustomException(ErrorCode.JSON_PARSING_ERROR);
            }

            // 'amount' 노드 확인
            JsonNode amount = transactions.get(0).path("amount");
            if (amount.isMissingNode()) {
                throw new CustomException(ErrorCode.JSON_PARSING_ERROR);
            }

            // 'total' 값 추출
            return amount.path("total").asInt();

        } catch (Exception e) {
            throw new CustomException(ErrorCode.JSON_PARSING_ERROR);
        }
    }
}
