package com.rest.api.auth.dto;

import domain.auth.Seller.Seller;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@ToString
public class LoginInfoDto implements UserDetails { // Token validation에 사용할 user dto, 의존성 때문에 customer module에 정의해놓음.

    private Long number;
    private String loginId;
    private String loginPwd;
    private String name;
    private String phoneNumber;
    private String email;
    private Collection<GrantedAuthority> roles;

    public LoginInfoDto() {
        this.number = -1L;
        this.name = "anonymous";
        this.loginPwd = "temp";
    }

    public LoginInfoDto(Seller seller) {
        this.number = seller.getSellerId();
        this.loginId = seller.getLoginId();
        this.name = seller.getName();    // 이름 -> 나중에 다시 설정해주기
        this.phoneNumber = seller.getPhoneNumber();
        this.email = seller.getEmail();
        roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(seller.getRole().getRole()));
    }

    public boolean isLoggedIn() {
        return (number != -1 && name != "anonymous");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    public String getLoginId() { return loginId; }
    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public String getPassword() {
        return loginPwd;
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
