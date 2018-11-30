package ru.configuration.authentication;

import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import ru.constant.UserRole;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements org.springframework.security.core.userdetails.UserDetails {
    @Setter
    private String password;
    @Setter
    private String username;

    private GrantedAuthority authority;


    public void setAuthority(UserRole authority) {

        this.authority = authority;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
}
