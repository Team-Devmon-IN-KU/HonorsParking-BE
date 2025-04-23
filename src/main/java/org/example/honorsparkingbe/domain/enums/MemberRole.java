package org.example.honorsparkingbe.domain.enums;

import org.springframework.security.core.GrantedAuthority;

public enum MemberRole implements GrantedAuthority {
    ROLE_NONE,
    ROLE_ADMIN,
    ROLE_USER;

    @Override
    public String getAuthority() {
        return name(); // 권한 이름 반환
    }
}
