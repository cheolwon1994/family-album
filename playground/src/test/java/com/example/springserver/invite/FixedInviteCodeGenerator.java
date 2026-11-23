package com.example.springserver.invite;

final class FixedInviteCodeGenerator implements InviteCodeGenerator {
    private final String code;

    FixedInviteCodeGenerator(String code) {
        this.code = code;
    }

    @Override
    public String generate() {
        return code;
    }
}
