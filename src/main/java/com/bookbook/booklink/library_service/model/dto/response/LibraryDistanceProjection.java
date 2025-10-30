package com.bookbook.booklink.library_service.model.dto.response;


import com.bookbook.booklink.library_service.model.Library;

public interface LibraryDistanceProjection {
    Library getLibrary();

    Double getDistance();
}