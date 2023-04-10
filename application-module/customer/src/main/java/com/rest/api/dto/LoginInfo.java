package com.rest.api.dto;

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
public class LoginInfo implements UserDetails { // Token validation에 사용할 user dto, 의존성 때문에 customer에 정의해놓음.

    private Long number;
    private String providerUserId;
    private String password;
    private String name;
    private Collection<GrantedAuthority> roles;

    public LoginInfo() {
        this.number = -1L;
        this.name = "anonymous";
        this.password = "temp";
    }

    public LoginInfo(User user) {
        this.number = user.getUserId();
        this.providerUserId = user.getProviderUserId();
        this.name = user.getNickName();
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
