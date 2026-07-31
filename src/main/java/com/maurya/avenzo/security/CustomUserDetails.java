package com.maurya.avenzo.security;

import com.maurya.avenzo.entity.UserEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UserEntity userEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /*
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name())
        );
        */

        List<GrantedAuthority> authorities = new ArrayList<>();

        // role based on the name of role itself
        // example - ADMIN -> ROLE_ADMIN
        authorities.add(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole()));

        // authority based on permission derived from role
        // example - ADMIN -> EVENT_CREATE, EVENT_DELETE, EVENT_UPDATE and so on
        userEntity.getRole().getPermissions().forEach(permission -> authorities.add(
                new SimpleGrantedAuthority(permission.name())
        ));

        return authorities;
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getEmail();
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

}
