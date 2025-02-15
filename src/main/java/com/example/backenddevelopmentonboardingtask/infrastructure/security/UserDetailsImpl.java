package com.example.backenddevelopmentonboardingtask.infrastructure.security;

import com.example.backenddevelopmentonboardingtask.domain.User;
import com.example.backenddevelopmentonboardingtask.domain.UserRoleEnum;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class UserDetailsImpl implements UserDetails {

  private final User user;

  public UserDetailsImpl(User user) {
    this.user = user;
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getUsername();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    boolean isAdmin = user.getAuthorities().stream()
        .anyMatch(UserRoleEnum.ADMIN::equals);

    String authority;

    if (isAdmin) {
      authority = UserRoleEnum.ADMIN.getAuthorityName();
    } else {
      authority = UserRoleEnum.USER.getAuthorityName();
    }

    SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(simpleGrantedAuthority);

    return authorities;
  }
}
