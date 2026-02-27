package com.bookbook.booklink.book_service.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class NationalLibraryResponseDto {
    @JsonProperty("TITLE")
    private String title;
    @JsonProperty("AUTHOR")
    private String author;

    @JsonProperty("EA_ISBN")
    private String isbn;

    @JsonProperty("PUBLISHER")
    private String publisher;

    @JsonProperty("PRE_PRICE")
    private String originalPrice;

    @JsonProperty("REAL_PUBLISH_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private LocalDate publishedDate;

    public BookResponseDto toBookResponseDto() {
        return BookResponseDto.builder()
                .title(title)
                .author(author)
                .publisher(publisher)
                .ISBN(isbn)
                .originalPrice(Integer.parseInt(originalPrice))
                .publishedDate(publishedDate)
                .build();
    }
}
