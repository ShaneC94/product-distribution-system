package com.pds.location.repository;

import com.pds.location.model.GeocodeCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface GeocodeCacheRepository extends JpaRepository<GeocodeCache, Long> {

    Optional<GeocodeCache> findByNormalizedAddress(String normalizedAddress);

    @Transactional
    @Modifying
    @Query("UPDATE GeocodeCache g SET g.lastAccessed = CURRENT_TIMESTAMP WHERE g.normalizedAddress = :address")
    int touchLastAccess(String address);
}
