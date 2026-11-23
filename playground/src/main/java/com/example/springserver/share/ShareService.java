package com.example.springserver.share;

import com.example.springserver.media.MediaItem;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShareService {
    private final MediaShareRepository shares;
    private final ShareTokenGenerator tokenGenerator;
    private final Clock clock;

    @Autowired
    public ShareService(MediaShareRepository shares, ShareTokenGenerator tokenGenerator) {
        this(shares, tokenGenerator, Clock.systemUTC());
    }

    ShareService(MediaShareRepository shares, ShareTokenGenerator tokenGenerator, Clock clock) {
        this.shares = shares;
        this.tokenGenerator = tokenGenerator;
        this.clock = clock;
    }

    @Transactional
    public String create(MediaItem media, Duration lifetime) {
        if (media == null) throw new IllegalArgumentException("공유할 파일을 찾을 수 없습니다.");
        if (lifetime == null || lifetime.isZero() || lifetime.isNegative()) {
            throw new IllegalArgumentException("공유 링크 유효 기간이 올바르지 않습니다.");
        }
        Instant now = Instant.now(clock);
        String token = tokenGenerator.generate();
        shares.save(MediaShare.create(hash(token), media, now, now.plus(lifetime)));
        return token;
    }

    @Transactional(readOnly = true)
    public MediaShare resolve(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("공유 링크가 올바르지 않습니다.");
        }
        MediaShare share = shares.findByTokenHash(hash(token.trim()))
                .orElseThrow(() -> new IllegalArgumentException("공유 링크가 올바르지 않습니다."));
        if (share.getRevokedAt() != null || !share.getExpiresAt().isAfter(Instant.now(clock))) {
            throw new IllegalArgumentException("만료되었거나 취소된 공유 링크입니다.");
        }
        return share;
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception exception) {
            throw new IllegalStateException("공유 링크 처리에 실패했습니다.", exception);
        }
    }
}
