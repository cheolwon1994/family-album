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
public class AdminBootstrap implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);
    private static final String LOCAL_ADMIN_EMAIL = "admin@localhost";

    private final UserAccountRepository users;
    private final PasswordEncoder encoder;
    private final Environment environment;
    private final String configuredEmail;
    private final String configuredPassword;

    public AdminBootstrap(UserAccountRepository users, PasswordEncoder encoder, Environment environment,
                          @Value("${app.bootstrap.admin-email:}") String email,
                          @Value("${app.bootstrap.admin-password:}") String password) {
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
            throw new IllegalStateException("배포 환경에서는 ADMIN_EMAIL과 ADMIN_PASSWORD를 설정해야 합니다.");
        }
        String email = configuredEmail.isBlank() ? LOCAL_ADMIN_EMAIL : configuredEmail.trim().toLowerCase(Locale.ROOT);
        var existing = users.findByEmailIgnoreCase(email);
        boolean generatedPassword = localProfile && configuredPassword.isBlank();
        String password = generatedPassword ? generatePassword() : configuredPassword;
        if (password.isBlank() || password.length() < 10) {
            throw new IllegalStateException("ADMIN_PASSWORD는 10자 이상이어야 합니다.");
        }

        UserAccount admin = existing.orElseGet(() -> new UserAccount(email, encoder.encode(password), UserAccount.Role.ADMIN));
        admin.setRole(UserAccount.Role.ADMIN);
        if (existing.isPresent() || generatedPassword) {
            admin.setPasswordHash(encoder.encode(password));
        }
        users.save(admin);
        if (generatedPassword) {
            log.warn("Local admin password for '{}' (valid until the next local restart): {}", email, password);
        }
    }

    private String generatePassword() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
