package com.rest.api.auth.dto;

import domain.auth.User.User;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@ToString
public class LoginInfoDto implements UserDetails { // Token validation에 사용할 user dto, 의존성 때문에 customer에 정의해놓음.

    private Long number;
    private String providerUserId;
    private String name;
    private String password;    // 안쓰임
    private Collection<GrantedAuthority> roles;

    public LoginInfoDto() {
        this.number = -1L;
        this.name = "anonymous";
        this.password = "temp";
    }

    public LoginInfoDto(User user) {
        this.number = user.getUserId();
        this.providerUserId = user.getProviderUserId();
        this.name = user.getUserName();
        roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(user.getRole().getRole()));
    }

    public boolean isLoggedIn() {
        return (number != -1 && name != "anonymous");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    public String getProviderUserId() { return providerUserId; }
    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
