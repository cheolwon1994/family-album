package com.example.springserver.media;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaItemRepository extends JpaRepository<MediaItem, Long> {
    List<MediaItem> findAllByOrderByUploadedAtDesc();
}
