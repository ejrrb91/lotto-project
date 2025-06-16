package com.example.lotto_project.security;

import com.example.lotto_project.domain.User;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails {

  //Getter를 사용하지 않는 이유 => 명확한 위임을 하기 때문에 필요 없음.
  //Setter를 사용하지 않는 이유 => 불변성을 유지해야 하기 때문에
  private final User user;

  public UserDetailsImpl(User user) {
    this.user = user;
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
}
