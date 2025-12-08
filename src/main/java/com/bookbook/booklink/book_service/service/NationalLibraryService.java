package com.bookbook.booklink.book_service.service;

import com.bookbook.booklink.book_service.model.dto.response.NationalLibraryResponseDto;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NationalLibraryService {

    @Value("${national-library.api-url}")
    private String apiUrl;

    @Value("${national-library.cert-key}")
    private String certKey;

    private final ObjectMapper objectMapper;

    /**
     * 국립중앙도서관 ISBN 검색
     *
     * @param isbn ISBN 코드
     * @return JsonNode (응답 데이터)
     * @throws Exception 예외
     */
    public NationalLibraryResponseDto searchBookByIsbn(String isbn, String traceId, UUID userId) throws Exception {
        log.info("[NationalLibraryService] [traceId = {}, userId = {}] get book from national library initiate isbn={}", traceId, userId, isbn);

        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?cert_key=").append(certKey);
        urlBuilder.append("&result_style=json");
        urlBuilder.append("&page_no=1");
        urlBuilder.append("&page_size=10");
        urlBuilder.append("&isbn=").append(URLEncoder.encode(isbn, StandardCharsets.UTF_8));

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("API 요청 실패 : HTTP error code : " + responseCode);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            JsonNode root = objectMapper.readTree(response.toString());

            if (!root.path("TOTAL_COUNT").asText().equals("1")) {
                throw new CustomException(ErrorCode.INVALID_ISBN_CODE);
            }

            JsonNode docs = root.path("docs");
            if (!docs.isArray() || docs.isEmpty()) {
                throw new CustomException(ErrorCode.INVALID_ISBN_CODE);
            }

            NationalLibraryResponseDto dto = objectMapper.treeToValue(docs.get(0), NationalLibraryResponseDto.class);
            log.info("[NationalLibraryService] [traceId = {}, userId = {}] get book from national library success isbn={}", traceId, userId, isbn);

            return dto;
        } finally {
            conn.disconnect();
        }
    }
}
