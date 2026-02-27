package com.bookbook.booklink.book_service.model;

import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BookCategory {
    GENERALITIES("000", "총류"),
    PHILOSOPHY("100", "철학"),
    RELIGION("200", "종교"),
    SOCIAL_SCIENCES("300", "사회과학"),
    NATURAL_SCIENCES("400", "자연과학"),
    TECHNOLOGY("500", "기술과학"),
    ARTS("600", "예술"),
    LANGUAGE("700", "언어"),
    LITERATURE("800", "문학"),
    HISTORY("900", "역사");

    public static BookCategory getByCode(String code) {
        for (BookCategory category : BookCategory.values()) {
            if (category.categoryCode.equals(code)) {
                return category;
            }
        }
        throw new CustomException(ErrorCode.INVALID_CATEGORY_CODE);
    }

    private final String categoryCode;
    private final String categoryName;
}
