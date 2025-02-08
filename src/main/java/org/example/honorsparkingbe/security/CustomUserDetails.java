package org.example.honorsparkingbe.security;

/**
 * Spring Security에서 요구하는 사용자 정보의 "표준 형식"을 구현
 * - Spring Security가 인증 및 권한 관리를 할 수 있도록 사용자 정보를 제공
 * - UserEntity 객체를 기반으로 사용자 정보(username, password, 권한 등)를 제공
 */

import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final MemberEntity memberEntity;

    public CustomUserDetails(MemberEntity memberEntity) {
        this.memberEntity = memberEntity;
    }

    // 사용자의 권한 목록 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // MemberRole이 GrantedAuthority를 구현하고 있으므로 바로 반환 가능
        return List.of(memberEntity.getRole());
    }

    @Override
    public String getPassword() {
        return memberEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return memberEntity.getAuthId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId(){
        return memberEntity.getId();
    }
}
