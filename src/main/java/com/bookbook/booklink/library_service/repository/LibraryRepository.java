package com.bookbook.booklink.library_service.repository;

import com.bookbook.booklink.library_service.model.Library;
import com.bookbook.booklink.library_service.model.dto.response.LibraryDistanceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LibraryRepository extends JpaRepository<Library, UUID> {

    /**
     * 현재 위치(lat, lng)를 기준으로 가장 가까운 순서로 도서관을 조회하고 페이지네이션 적용.
     * name 조건이 있으면 이름으로 필터링도 수행.
     *
     * @param lat      현재 위도
     * @param lng      현재 경도
     * @param name     검색할 도서관 이름 (선택적)
     * @param pageable 페이지네이션 및 정렬 정보
     * @return 페이지네이션된 Library 목록
     */
    @Query(value = """
                SELECT
                    l as library,
                    (6371 * acos(
                        cos(radians(:lat)) * cos(radians(l.latitude)) * cos(radians(l.longitude) - radians(:lng))
                        + sin(radians(:lat)) * sin(radians(l.latitude))
                    )) as distance
                FROM Library l
                WHERE (:name IS NULL OR l.name LIKE %:name%)
                ORDER BY distance ASC
            """, countQuery = """
                SELECT count(l) FROM Library l WHERE (:name IS NULL OR l.name LIKE %:name%)
            """)
    Page<LibraryDistanceProjection> findLibrariesOrderByDistance(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("name") String name,
            Pageable pageable
    );


    @Query(value = "SELECT * FROM library " +
            "WHERE member_id = :userId ",
            nativeQuery = true)
    Optional<Library> findByMemberId(@Param("userId") UUID userId);
}
    