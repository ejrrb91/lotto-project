package com.example.lotto_project.security;

import com.example.lotto_project.domain.User;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class UserDetailsImpl implements UserDetails, OAuth2User {

  //Getter를 사용하지 않는 이유 => 명확한 위임을 하기 때문에 필요 없음.
  //Setter를 사용하지 않는 이유 => 불변성을 유지해야 하기 때문에
  private final User user;
  private Map<String, Object> attributes; //OAuth2 사용자 정보를 담을 필드

  //일반 로그인을 위한 생성자
  public UserDetailsImpl(User user) {
    this.user = user;
  }

  //OAuth2 로그인을 위한 생성자
  public UserDetailsImpl(User user, Map<String, Object> attributes) {
    this.user = user;
    this.attributes = attributes;
  }

  public User getUser() {
    return user;
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    //TODO : 지금은 사용자 권한(Role)을 사용하지 않으므로, 빈 리스트를 반환, 추후 수정 예정
    return Collections.emptyList();
  }

  //TODO : 아래 4개는 계정 상태에 관한 설정, 지금은 모두 true로 설정, 추후 수정 예정
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

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public String getName() {
    // OAuth2에서는 보통 사용자의 주요 식별자(ID)를 반환하지만,
    // 지금 우리 서비스에서는 특별히 사용하지 않으므로 null을 반환 무관함.
    return user.getId().toString();
  }
}
