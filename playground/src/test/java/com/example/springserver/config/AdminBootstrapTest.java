package com.example.springserver.config;

import com.example.springserver.SpringServerApplication;
import com.example.springserver.account.UserAccount;
import com.example.springserver.account.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.DefaultApplicationArguments;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SpringServerApplication.class, properties = {
        "spring.datasource.url=jdbc:h2:mem:bootstrap-test;DB_CLOSE_DELAY=-1"
})
class AdminBootstrapTest {
    @Autowired private UserAccountRepository users;
    @Autowired private AdminBootstrap bootstrap;

    @Test
    void createsLocalAdminInH2WhenNoCredentialsAreConfigured() {
        UserAccount admin = users.findByEmailIgnoreCase("admin@localhost").orElseThrow();
        assertThat(admin.getRole()).isEqualTo(UserAccount.Role.ADMIN);
        assertThat(admin.getPasswordHash()).startsWith("$2a$");
    }

    @Test
    void regeneratesAndStoresLocalAdminPasswordOnEveryStartup() {
        UserAccount admin = users.findByEmailIgnoreCase("admin@localhost").orElseThrow();
        String previousHash = admin.getPasswordHash();

        bootstrap.run(new DefaultApplicationArguments(new String[0]));

        UserAccount refreshedAdmin = users.findByEmailIgnoreCase("admin@localhost").orElseThrow();
        assertThat(refreshedAdmin.getPasswordHash()).startsWith("$2a$").isNotEqualTo(previousHash);
    }
}
