package com.example.lotto_project.service;

import com.example.lotto_project.domain.User;
import com.example.lotto_project.repository.UserRepository;
import com.example.lotto_project.security.UserDetailsImpl;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  //OAuth2 로그인 성공 시, Spring Security가 loadUser() 메서드를 호출하여 사용자 정보를 가져옴.
  @Override
  public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {

    //1. 부모 클래스의 loadUser()를 호출하여 Google로부터 사용자 정보를 포함한 OAuth2User 객체를 받아옴.
    OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

    //2. 받아온 사용자 정보에서 'attributes'를 추출함
    //이메일, 이름, 프로필 사진 등의 정보가 Map의 형태로 있음.
    Map<String, Object> attributes = oAuth2User.getAttributes();

    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");

    //3. DB에 해당 메일을 가진 사용자가 있는지 확인
    User user = userRepository.findByEmail(email)
        //.orElseGet() : 사용자가 존재하면 그 정보를 사용하고, 존재하지 않으면 (->) 뒤의 로직을 실행.
        .orElseGet(() -> {
          User newUser = new User();
          newUser.setEmail(email);
          newUser.setNickname(name);
          //소셜 로그인 사용자는 비밀번호를 직접 사용하지 않으므로, 임의의 값을 암호화 하여 자장함.
          //DB의 password 컬럼의 not-null 제약조건을 만족시키기 위해
          newUser.setPassword(passwordEncoder.encode("social-login-password"));
          return userRepository.save(newUser);
    });

    //5. User 객체를 Spring Security가 이해할 수 있는 UserDetails의 형태로 감싸서 리턴
    //이 객체는 Spring Security의 SecurityContext에 저장되어 인증된 사용자로 관리됨.
    return new UserDetailsImpl(user, attributes);
  }
}
