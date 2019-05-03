package com.fis.is.terminy.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.groups.Default;

@Entity
public class Client extends BaseEntity {
    @Column
    @NotBlank(message = "NB", groups = {editEntity.class, Default.class})
    private String phone;

    @Column(unique = true)
    @NotBlank(message = "NB", groups = {editEntity.class, Default.class})
    @Email(groups = editEntity.class)
    private String mail;

    @Column
    @NotBlank(message = "NB")
    private String name;

    @Column
    @NotBlank(message = "NB")
    private String surname;


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
