package com.fis.is.terminy.models;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
public class Privilege implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String privilege;

    @Override
    public String getAuthority() {
        return privilege;
    }

    @Override
    public String toString(){
        return privilege;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }
}
