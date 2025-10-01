// 관리자 권한 종류 정의
package com.wasd.smartWMS.domain.admins;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    MAIN("ROLE_MAIN", "메인 관리자"),    // MAIN 관리자
    SUB("ROLE_SUB", "서브 관리자");    // SUB 관리자

    private final String key;
    private final String title;
}