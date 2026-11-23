package com.example.springserver.invite;

import com.example.springserver.account.UserAccount;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InviteServiceTest {
    @Mock
    private InviteCodeRepository invites;

    @Test
    void createsInviteWithCodeFromInjectedGenerator() {
        InviteCodeGenerator generator = new FixedInviteCodeGenerator("fixed-invite-code");
        InviteService service = new InviteService(invites, generator);
        UserAccount admin = new UserAccount("admin@example.com", "hash", UserAccount.Role.ADMIN);

        String createdCode = service.create(admin);

        assertThat(createdCode).isEqualTo("fixed-invite-code");
        ArgumentCaptor<InviteCode> saved = ArgumentCaptor.forClass(InviteCode.class);
        verify(invites).save(saved.capture());
        assertThat(saved.getValue().getCodeHash()).hasSize(64);
    }

    @Test
    void rejectsNonAdminBeforeGeneratingCode() {
        InviteCodeGenerator generator = () -> {
            throw new AssertionError("generator should not be called");
        };
        InviteService service = new InviteService(invites, generator);
        UserAccount member = new UserAccount("member@example.com", "hash", UserAccount.Role.MEMBER);

        assertThatThrownBy(() -> service.create(member))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("관리자만 초대 코드를 만들 수 있습니다.");
        verify(invites, never()).save(any(InviteCode.class));
    }

    @Test
    void consumesCodeOnlyWhenStoredInviteExists() {
        InviteService service = new InviteService(invites, new FixedInviteCodeGenerator("unused"));
        org.mockito.Mockito.when(invites.findByCodeHashForUpdate(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consume("missing-code"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용할 수 없는 초대 코드입니다.");
    }
}
