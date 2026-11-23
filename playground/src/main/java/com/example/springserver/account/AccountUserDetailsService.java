package com.example.springserver.account;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountUserDetailsService implements UserDetailsService {
    private final UserAccountRepository users;

    public AccountUserDetailsService(UserAccountRepository users) { this.users = users; }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email) {
        return users.findByEmailIgnoreCase(email)
                .map(account -> User.withUsername(account.getEmail())
                        .password(account.getPasswordHash())
                        .roles(account.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
