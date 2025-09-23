package com.bookbook.booklink.library_service.repository;

import com.bookbook.booklink.library_service.model.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LibraryRepository extends JpaRepository<Library, UUID> {

    @Query(value = "SELECT * FROM library " +
            "WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(:lon)) + " +
            "sin(radians(:lat)) * sin(radians(latitude)))) < 3",
            nativeQuery = true)
    List<Library> findNearbyLibraries(@Param("lat") Double lat, @Param("lon") Double lon);

}
    