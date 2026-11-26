package com.example.springserver.share;

final class FixedShareTokenGenerator implements ShareTokenGenerator {
    private final String token;

    FixedShareTokenGenerator(String token) {
        this.token = token;
    }

    @Override
    public String generate() {
        return token;
    }
}
