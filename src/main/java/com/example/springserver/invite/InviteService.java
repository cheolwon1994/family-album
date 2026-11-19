package com.example.springserver.invite;

import com.example.springserver.account.UserAccount;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InviteService {
    private final InviteCodeRepository invites;
    private final SecureRandom random = new SecureRandom();

    public InviteService(InviteCodeRepository invites) { this.invites = invites; }

    @Transactional
    public String create(UserAccount creator) {
        if (creator.getRole() != UserAccount.Role.ADMIN) {
            throw new IllegalStateException("관리자만 초대 코드를 만들 수 있습니다.");
        }
        byte[] bytes = new byte[18];
        random.nextBytes(bytes);
        String code = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        invites.save(new InviteCode(hash(code), Instant.now()));
        return code;
    }

    @Transactional
    public void consume(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("초대 코드를 입력해 주세요.");
        }
        InviteCode invite = invites.findByCodeHashForUpdate(hash(code.trim()))
                .orElseThrow(() -> new IllegalArgumentException("사용할 수 없는 초대 코드입니다."));
        if (invite.getUsedAt() != null) {
            throw new IllegalArgumentException("이미 사용된 초대 코드입니다.");
        }
        invite.markUsed(Instant.now());
    }

    private String hash(String code) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(code.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception exception) {
            throw new IllegalStateException("초대 코드 처리에 실패했습니다.", exception);
        }
    }
}
