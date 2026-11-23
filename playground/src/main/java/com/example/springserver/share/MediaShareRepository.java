package com.example.springserver.share;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaShareRepository extends JpaRepository<MediaShare, Long> {
    Optional<MediaShare> findByTokenHash(String tokenHash);
}
