package org.example.honorsparkingbe.security;
/**
 * OAuth2 핵심 로직 카카오, 구글, 네이버 로그인 및 회원가입
 */

import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.LoginPlatform;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.dto.*;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
  //DefaultOAuth2UserService OAuth2UserService의 구현체

  private final MemberRepository memberRepository;

  public CustomOAuth2UserService(MemberRepository memberRepository) {

    this.memberRepository = memberRepository;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    OAuth2User oAuth2User = super.loadUser(userRequest);
    System.out.println(oAuth2User.getAttributes());

    // OAuth provider
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    OAuth2Response oAuth2Response = null;

    String role = "ROLE_NONE";
    Long memberId = null;

    // Provider마다 데이터를 주는 방식이 다르기에 다르게 처리(8강)
    if (registrationId.equals("naver")) {
      oAuth2Response = new NaverResponse(oAuth2User.getAttributes());

      String authId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
      MemberEntity existData = memberRepository.findByAuthId(authId);
      String name = oAuth2Response.getName();

      if (existData == null) { // 회원가입인 경우

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setAuthId(authId);
        memberEntity.setEmail(oAuth2Response.getEmail());
        memberEntity.setRole(MemberRole.valueOf(role));
        memberEntity.setUserName(name);

        NaverResponse naverResponse = (NaverResponse) oAuth2Response;
        memberEntity.setPhoneNumber(naverResponse.getMobile()); // 전화번호
        memberEntity.setBirthday(naverResponse.getBirthday()); // 생일
        memberEntity.setBirthdayYear(
            naverResponse.getBirthyear() != null ? Integer.parseInt(naverResponse.getBirthyear())
                : 0
        );
        memberEntity.setLoginPlatform(LoginPlatform.NAVER);

        memberRepository.save(memberEntity);
        memberId = memberEntity.getId();
      } else {
        memberId = existData.getId();
        role= existData.getRole().toString();
      }
    } else if (registrationId.equals("google")) {
      oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

      String authId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
      MemberEntity existData = memberRepository.findByAuthId(authId);
      String name = oAuth2Response.getName();

      if (existData == null) { // 회원가입인 경우

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setAuthId(authId);
        memberEntity.setEmail(oAuth2Response.getEmail());
        memberEntity.setRole(MemberRole.valueOf(role));
        memberEntity.setUserName(name);
        memberEntity.setLoginPlatform(LoginPlatform.GOOGLE);

        memberRepository.save(memberEntity);
        memberId = memberEntity.getId();
      } else {
        memberId = existData.getId();
        role= existData.getRole().toString();
      }
    } else if (registrationId.equals("kakao")) {
      oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());

      String authId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
      MemberEntity existData = memberRepository.findByAuthId(authId);
      String name = oAuth2Response.getName();

      if (existData == null) { // 회원가입인 경우

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setAuthId(authId);
        memberEntity.setEmail(oAuth2Response.getEmail());
        memberEntity.setRole(MemberRole.valueOf(role));
        memberEntity.setUserName(name);
        memberEntity.setLoginPlatform(LoginPlatform.KAKAO);

        KakaoResponse kakaoResponse = (KakaoResponse) oAuth2Response;
        memberEntity.setBirthday(kakaoResponse.getBirthday());
        memberEntity.setBirthdayYear(
            kakaoResponse.getBirthYear() != null ? kakaoResponse.getBirthYear() : 0
        );
        memberEntity.setPhoneNumber(kakaoResponse.getPhoneNumber());

        memberRepository.save(memberEntity);
        memberId = memberEntity.getId();
      } else {
        memberId = existData.getId();
        role= existData.getRole().toString();
      }
    } else {
      return null;
    }
    return new CustomOAuth2User(oAuth2Response, role, memberId);
  }
}
