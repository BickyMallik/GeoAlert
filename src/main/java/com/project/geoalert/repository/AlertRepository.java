package com.project.geoalert.repository;

import com.project.geoalert.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query(value = """
            SELECT * FROM alerts
            WHERE ST_DWithin(
                location::geography,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                :radiusMeters
            )
            """, nativeQuery = true)
    List<Alert> findAlertsWithinRadius(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusMeters") Double radiusMeters
    );

    @Query(value = "SELECT COUNT(*) > 0 FROM alerts WHERE type = :type AND DATE(created_at) = CURRENT_DATE", nativeQuery = true)
    boolean existsTodayByType(@Param("type") String type);
}