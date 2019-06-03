package com.fis.is.terminy.models;

import com.fis.is.terminy.validation.annotations.UniqueEmailCheck;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.groups.Default;

@Entity
public class Client extends BaseEntity {
    @Column
    @Pattern(regexp = "\\+*[0-9 -]{4,15}",
    message = "podaj poprawny numer telefonu", groups = {editEntity.class, Default.class})
    @NotBlank(message = "Uzupełnij pole", groups = {editEntity.class, Default.class})
    private String phone;

    @Column(unique = true)
    @NotBlank(message = "Uzupełnij pole", groups = {editEntity.class, Default.class})
    @Email(groups = {editEntity.class, Default.class}, message = "podaj poprawny adres email")
    @UniqueEmailCheck(message = "użytkownik o takim mailu istnieje")
    private String mail;

    @Column
    @NotBlank(message = "Uzupełnij pole")
    private String name;

    @Column
    @NotBlank(message = "Uzupełnij pole")
    private String surname;

    @Column
    private String resetToken;

    public String getResetToken() { return resetToken;}

    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

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
