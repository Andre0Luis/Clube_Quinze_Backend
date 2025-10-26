package br.com.clube_quinze.api.security;

import br.com.clube_quinze.api.model.enumeration.RoleType;
import br.com.clube_quinze.api.model.user.User;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class ClubeQuinzeUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final RoleType role;

    private ClubeQuinzeUserDetails(Long id, String email, String passwordHash, RoleType role) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public static ClubeQuinzeUserDetails from(User user) {
        Objects.requireNonNull(user, "user");
        return new ClubeQuinzeUserDetails(user.getId(), user.getEmail(), user.getPasswordHash(), user.getRole());
    }

    public Long getId() {
        return id;
    }

    public RoleType getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority = "ROLE_" + role.name();
        return List.of(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
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
