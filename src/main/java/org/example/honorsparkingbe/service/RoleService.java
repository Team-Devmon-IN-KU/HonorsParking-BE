package org.example.honorsparkingbe.service;

import jakarta.servlet.http.HttpSession;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.dto.CustomOAuth2User;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.example.honorsparkingbe.security.util.SecurityUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final MemberRepository memberRepository;

    public RoleService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void convertRole(HttpSession session) {
        // 1. DB 값 ROLE_USER로 변경
        Long id= SecurityUtil.getCurrentUserId();

        if(id==null){
            throw new IllegalArgumentException("Session not exist");
        }
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.setRole(MemberRole.ROLE_USER);
        memberRepository.save(member);

        // 2. 세션 갱신
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = currentAuth.getPrincipal();

        if (principal instanceof CustomOAuth2User oldUser) {
            CustomOAuth2User updatedUser = new CustomOAuth2User(
                    oldUser.getOAuth2Response(),
                    MemberRole.ROLE_USER.name(),
                    oldUser.getId()
            );

            var newAuth = new UsernamePasswordAuthenticationToken(
                    updatedUser,
                    null,
                    List.of(new SimpleGrantedAuthority(MemberRole.ROLE_USER.name()))
            );

            SecurityContextHolder.getContext().setAuthentication(newAuth);

            // Redis 세션까지 갱신되도록 HttpSession에 보관된 SecurityContext 갱신
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        }
    }
}
