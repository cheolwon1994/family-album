package com.example.springserver.share;

@FunctionalInterface
public interface ShareTokenGenerator {
    String generate();
}
