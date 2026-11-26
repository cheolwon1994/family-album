package com.example.springserver.config;

import com.example.springserver.account.UserAccount;
import com.example.springserver.account.UserAccountRepository;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MemberBootstrap implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(MemberBootstrap.class);
    private static final String DEFAULT_LOCAL_MEMBER_EMAIL = "member@localhost";

    private final UserAccountRepository users;
    private final PasswordEncoder encoder;
    private final Environment environment;
    private final String configuredEmail;
    private final String configuredPassword;

    public MemberBootstrap(UserAccountRepository users, PasswordEncoder encoder, Environment environment,
                           @Value("${app.bootstrap.member-email:}") String email,
                           @Value("${app.bootstrap.member-password:}") String password) {
        this.users = users;
        this.encoder = encoder;
        this.environment = environment;
        this.configuredEmail = email;
        this.configuredPassword = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean localProfile = environment.acceptsProfiles(Profiles.of("local"));
        if (!localProfile && (configuredEmail.isBlank() || configuredPassword.isBlank())) {
            return;
        }

        String email = configuredEmail.isBlank()
                ? DEFAULT_LOCAL_MEMBER_EMAIL
                : configuredEmail.trim().toLowerCase(Locale.ROOT);
        boolean generatedPassword = localProfile && configuredPassword.isBlank();
        String password = generatedPassword ? generatePassword() : configuredPassword;
        if (password.isBlank() || password.length() < 10) {
            throw new IllegalStateException("MEMBER_PASSWORD는 10자 이상이어야 합니다.");
        }

        var existing = users.findByEmailIgnoreCase(email);
        if (existing.isPresent() && existing.get().getRole() == UserAccount.Role.ADMIN) {
            throw new IllegalStateException("관리자 계정은 일반 사용자로 덮어쓸 수 없습니다.");
        }

        UserAccount member = existing.orElseGet(() ->
                new UserAccount(email, encoder.encode(password), UserAccount.Role.MEMBER));
        member.setRole(UserAccount.Role.MEMBER);
        if (existing.isPresent() || generatedPassword) {
            member.setPasswordHash(encoder.encode(password));
        }
        users.save(member);

        if (generatedPassword) {
            log.warn("Local member password for '{}' (valid until the next local restart): {}", email, password);
        }
    }

    private String generatePassword() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
