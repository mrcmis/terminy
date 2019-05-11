package com.fis.is.terminy.models;

import com.fis.is.terminy.validation.annotations.UniqueLoginCheck;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.HashSet;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    @NotBlank(message = "NB")
    @UniqueLoginCheck(message = "login already used")
    private String login;

    @Column
    @NotBlank(message = "NB")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = Privilege.class)
    @JoinTable(name = "user_privilege", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id"))
    private Collection<? extends GrantedAuthority> grantedPrivileges = new HashSet<Privilege>();


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<? extends GrantedAuthority> getGrantedPrivileges() {
        return grantedPrivileges;
    }

    public void setGrantedPrivileges(Collection<? extends GrantedAuthority> grantedPrivileges) {
        this.grantedPrivileges = grantedPrivileges;
    }

    @Override
    public String getUsername() {
        return login;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedPrivileges;
    }

    @Override
    public int hashCode(){
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null) return false;
        if(o.getClass() != getClass()) return false;
        BaseEntity other = (BaseEntity)o;

        return other.getId() == id;
    }

    /**
     * marker interface used to limit validation while editing user
     */
    public interface editEntity{}
}
