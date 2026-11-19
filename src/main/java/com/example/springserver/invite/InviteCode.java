package com.example.springserver.invite;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "invite_codes")
public class InviteCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String codeHash;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant usedAt;

    protected InviteCode() { }

    public InviteCode(String codeHash, Instant createdAt) {
        this.codeHash = codeHash;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUsedAt() { return usedAt; }
    public void markUsed(Instant instant) { this.usedAt = instant; }
}
