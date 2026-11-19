package com.example.springserver.media;

import com.example.springserver.account.UserAccount;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaService {
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif", "video/mp4", "video/webm", "video/quicktime");
    private final MediaItemRepository media;
    private final Path storage;

    public MediaService(MediaItemRepository media, @Value("${app.storage.location:./uploads}") String storageLocation) {
        this.media = media;
        this.storage = Path.of(storageLocation).toAbsolutePath().normalize();
    }

    @Transactional
    public void store(MultipartFile file, UserAccount uploader) throws IOException {
        String type = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_TYPES.contains(type)) throw new IllegalArgumentException("지원하는 사진 또는 영상 파일을 선택해 주세요.");
        if (file.isEmpty()) throw new IllegalArgumentException("빈 파일은 올릴 수 없습니다.");
        Files.createDirectories(storage);
        String key = UUID.randomUUID().toString();
        Files.copy(file.getInputStream(), storage.resolve(key));
        String name = Path.of(file.getOriginalFilename() == null ? "upload" : file.getOriginalFilename())
                .getFileName().toString();
        media.save(new MediaItem(name, key, type, Instant.now(), uploader));
    }

    public Resource load(MediaItem item) throws IOException {
        Path file = storage.resolve(item.getStorageKey()).normalize();
        if (!file.startsWith(storage)) throw new IOException("Invalid media path");
        Resource resource = new UrlResource(file.toUri());
        if (!resource.exists() || !resource.isReadable()) throw new IOException("Media file is unavailable");
        return resource;
    }
}
