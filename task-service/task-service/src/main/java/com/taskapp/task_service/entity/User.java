package com.taskapp.task_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String role = "ROLE_USER";

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // User owns many projects
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "owner")
    private List<Project> projects = new ArrayList<>();

    // User can be assigned many tasks
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "assignee")
    private List<Task> tasks = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }
    @Override
    public String getUsername() {
        return email;  // ← return email, not username!
    }
    public String getDisplayUsername() {
        return this.username;  // directly accesses the field, bypasses the override
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
