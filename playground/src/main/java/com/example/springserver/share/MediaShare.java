package com.example.springserver.share;

import com.example.springserver.media.MediaItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "media_shares")
public class MediaShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @ManyToOne(optional = false)
    private MediaItem media;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant revokedAt;

    protected MediaShare() { }

    private MediaShare(String tokenHash, MediaItem media, Instant createdAt, Instant expiresAt) {
        this.tokenHash = tokenHash;
        this.media = media;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public static MediaShare create(String tokenHash, MediaItem media, Instant createdAt, Instant expiresAt) {
        return new MediaShare(tokenHash, media, createdAt, expiresAt);
    }

    public Long getId() { return id; }
    public String getTokenHash() { return tokenHash; }
    public MediaItem getMedia() { return media; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getRevokedAt() { return revokedAt; }
    public void revoke(Instant instant) { this.revokedAt = instant; }
}
