package it.epicode.segnoNome.auth.entities;


import it.epicode.segnoNome.auth.enums.Role;
import it.epicode.segnoNome.modules.entities.Dictionary;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
public class AppUser implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "dictionary_id")
    )
    private List<Dictionary> favoritesD = new ArrayList<>();



    private String imgUrl;

    @Column(nullable = false)
    private boolean hasPaid = false;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Puoi personalizzarlo se necessario
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Puoi personalizzarlo se necessario
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Puoi personalizzarlo se necessario
    }

    @Override
    public boolean isEnabled() {
        return true; // Puoi personalizzarlo se necessario
    }
}


