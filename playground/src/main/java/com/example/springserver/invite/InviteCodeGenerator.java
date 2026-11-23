package com.example.springserver.invite;

/**
 * 초대 코드 발급 규칙을 표현하는 계약입니다.
 * 실제 발급기와 테스트용 고정 발급기를 교체할 수 있도록 InviteService에서 사용합니다.
 */
@FunctionalInterface
public interface InviteCodeGenerator {
    String generate();
}
