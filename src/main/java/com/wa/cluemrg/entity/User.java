package com.wa.cluemrg.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
public class User implements UserDetails {

    int id;
    String username;
    String password;
    String department;
    String role;

    // 表示获取登录用户所有权限
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 在这里从数据库中获取用户的角色信息，并将其转换为GrantedAuthority的集合返回
        Set<GrantedAuthority> authorities = new HashSet<>();
        String[] roleArray=role.split(",");
        for (String roleName : roleArray) {
            roleName="ROLE_"+roleName;
            authorities.add(new SimpleGrantedAuthority(roleName));
        }
        return authorities;
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
