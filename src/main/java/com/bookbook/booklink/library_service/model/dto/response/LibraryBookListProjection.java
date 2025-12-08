package com.bookbook.booklink.library_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LibraryBookListProjection {
    String getImageUrl();
    UUID getId();
    String getTitle();
    String getAuthor();
    String getLibraryName();
    Double getDistance();
    Integer getCopies();
    Integer getBorrowedCount();
    Integer getDeposit();
    Integer getRentedOut();
    LocalDateTime getExpectedReturnDate();
}
