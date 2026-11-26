package com.example.springserver.config;

import com.example.springserver.SpringServerApplication;
import com.example.springserver.account.UserAccount;
import com.example.springserver.account.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SpringServerApplication.class, properties = {
        "spring.datasource.url=jdbc:h2:mem:member-bootstrap-test;DB_CLOSE_DELAY=-1",
        "app.bootstrap.member-email=member@example.com",
        "app.bootstrap.member-password=member-password"
})
class MemberBootstrapTest {
    @Autowired private UserAccountRepository users;
    @Autowired private MemberBootstrap bootstrap;

    @Test
    void createsConfiguredMemberAccount() {
        UserAccount member = users.findByEmailIgnoreCase("member@example.com").orElseThrow();

        assertThat(member.getRole()).isEqualTo(UserAccount.Role.MEMBER);
        assertThat(member.getPasswordHash()).startsWith("$2a$");
    }

    @Test
    void updatesConfiguredMemberPasswordOnStartup() {
        String previousHash = users.findByEmailIgnoreCase("member@example.com").orElseThrow().getPasswordHash();

        bootstrap.run(new DefaultApplicationArguments(new String[0]));

        String refreshedHash = users.findByEmailIgnoreCase("member@example.com").orElseThrow().getPasswordHash();
        assertThat(refreshedHash).startsWith("$2a$").isNotEqualTo(previousHash);
    }
}
