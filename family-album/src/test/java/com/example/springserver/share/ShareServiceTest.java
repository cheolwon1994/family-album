package com.example.springserver.share;

import com.example.springserver.account.UserAccount;
import com.example.springserver.media.MediaItem;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShareServiceTest {
    private static final Instant NOW = Instant.parse("2026-11-23T00:00:00Z");

    @Mock
    private MediaShareRepository shares;

    @Test
    void createsExpiringShareWithInjectedTokenGenerator() {
        ShareService service = new ShareService(shares, new FixedShareTokenGenerator("share-token"),
                Clock.fixed(NOW, ZoneOffset.UTC));
        MediaItem media = new MediaItem("family.jpg", "stored-key", "image/jpeg", NOW,
                new UserAccount("owner@example.com", "hash", UserAccount.Role.ADMIN));

        String token = service.create(media, Duration.ofHours(24));

        assertThat(token).isEqualTo("share-token");
        ArgumentCaptor<MediaShare> saved = ArgumentCaptor.forClass(MediaShare.class);
        verify(shares).save(saved.capture());
        assertThat(saved.getValue().getTokenHash()).hasSize(64);
        assertThat(saved.getValue().getExpiresAt()).isEqualTo(NOW.plus(Duration.ofHours(24)));
    }

    @Test
    void rejectsExpiredShare() {
        ShareService service = new ShareService(shares, new FixedShareTokenGenerator("unused"),
                Clock.fixed(NOW, ZoneOffset.UTC));
        MediaShare expired = MediaShare.create("hash", null, NOW.minusSeconds(7200), NOW.minusSeconds(1));
        when(shares.findByTokenHash(any())).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> service.resolve("expired-token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("만료되었거나 취소된 공유 링크입니다.");
    }
}
