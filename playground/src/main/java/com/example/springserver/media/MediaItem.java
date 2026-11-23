package com.example.springserver.media;

import com.example.springserver.account.UserAccount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "media_items")
public class MediaItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false, unique = true)
    private String storageKey;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Instant uploadedAt;

    @ManyToOne(optional = false)
    private UserAccount uploadedBy;

    protected MediaItem() { }

    public MediaItem(String originalName, String storageKey, String contentType, Instant uploadedAt, UserAccount uploadedBy) {
        this.originalName = originalName;
        this.storageKey = storageKey;
        this.contentType = contentType;
        this.uploadedAt = uploadedAt;
        this.uploadedBy = uploadedBy;
    }

    public Long getId() { return id; }
    public String getOriginalName() { return originalName; }
    public String getStorageKey() { return storageKey; }
    public String getContentType() { return contentType; }
    public Instant getUploadedAt() { return uploadedAt; }
    public UserAccount getUploadedBy() { return uploadedBy; }
}
