package com.example.springserver.account;

import com.example.springserver.invite.InviteService;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    private final UserAccountRepository users;
    private final InviteService invites;
    private final PasswordEncoder passwordEncoder;

    public AccountService(UserAccountRepository users, InviteService invites, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.invites = invites;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(String email, String password, String code) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        if (normalizedEmail.isBlank() || password == null || password.length() < 10) {
            throw new IllegalArgumentException("이메일을 입력하고 비밀번호를 10자 이상 입력해 주세요.");
        }
        if (users.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        invites.consume(code);
        users.save(new UserAccount(normalizedEmail, passwordEncoder.encode(password), UserAccount.Role.MEMBER));
    }

    public UserAccount findRequiredByEmail(String email) {
        return users.findByEmailIgnoreCase(email).orElseThrow();
    }
}
