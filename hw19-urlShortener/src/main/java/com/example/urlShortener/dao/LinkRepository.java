package com.example.urlShortener.dao;

import com.example.urlShortener.entity.Link;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

    @Query("SELECT l FROM Link l WHERE l.shortCode = :shortCode")
    Optional<Link> findByShortCode(@Param("shortCode") String shortCode);

    @Modifying
    @Query("UPDATE Link l SET l.clickCount = l.clickCount + 1 WHERE l.id = :id")
    void incrementClickCount(@Param("id") Long id);

    @Query("SELECT l FROM Link l WHERE l.originalUrl LIKE %:keyword%")
    List<Link> findByUrlContaining(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM links ORDER BY click_count DESC LIMIT :limit", nativeQuery = true)
    List<Link> findTopPopular(@Param("limit") int limit);

    boolean existsByShortCode(String shortCode);
}
