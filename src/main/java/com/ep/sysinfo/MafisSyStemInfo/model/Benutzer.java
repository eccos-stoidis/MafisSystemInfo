package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Benutzer entity class with Lombok annotations
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "userRollen")  // Exclude userRollen to avoid potential circular references
public class Benutzer implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1991375623795012882L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    @NotEmpty(message = "Bitte geben Sie einen Namen ein!")
    @Size(min = 4, message = "Der Name muss mindestens 4 Zeichen lang sein!")
    private String name;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Bitte geben Sie einen Benutzernamen ein!")
    @Size(min = 4, message = "Der Benutzername muss mindestens 4 Zeichen lang sein!")
    private String username;

    @Column(nullable = false, unique = true)
    @Email(message = "Bitte geben Sie eine gültige Email ein!")
    @NotEmpty(message = "Bitte geben Sie eine gültige Email ein!")
    @Pattern(regexp = "^[^@\\s]+@eccos-pro\\.com$", message = "Die Email muss mit @eccos-pro.com enden.")
    private String email;


    @Column(nullable = false)
    @Size(min = 4, message = "Das Kennwort muss mindestens 4 Ziffern haben!")
    private String password;

    @Column
    private String bearbeitetVon;

    @Column
    private LocalDateTime lastModified;

    @Column
    private LocalDateTime angemeldetAm;

    @Column(length = 1)
    private String enabled;

    @OneToMany(mappedBy = "benutzer", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<BenutzerRolle> userRollen = new ArrayList<>();

    /**
     * Returns a collection of granted authorities based on the user's roles.
     *
     * @return          a collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userRollen.stream()
                .map(benutzerRolle -> new SimpleGrantedAuthority(benutzerRolle.getRolle().getRolleName()))
                .collect(Collectors.toSet());
    }

    /**
     * Indicates whether the user's account is non-expired.
     *
     * @return          true if the account is non-expired, false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user account is currently locked or not.
     *
     * @return         true if the account is not locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    /**
     * Indicates whether the user's credentials (password) are non-expired.
     *
     * @return          true if the credentials are non-expired, false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user account is enabled.
     *
     * @return         true if the account is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
