package it.univr.track.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import it.univr.track.entity.enumeration.Gender;
import it.univr.track.entity.enumeration.Role;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@Getter
@Setter
public class UserRegistered extends AbstractEntity {

    @Column(unique = true)
    protected String username;
    protected String password;
    protected String firstname;
    protected String lastname;
    @Setter(AccessLevel.NONE)
    protected String email;
    protected String address;
    protected String telephoneNumber;
    protected String city;
    protected String taxIdentificationNumber;
    @Enumerated(EnumType.STRING)
    protected Role role;
    @Enumerated(EnumType.STRING)
    protected Gender gender;

    public UserRegistered(String firstname, String lastname, String username, String password, String email, Role role, Gender gender, String city, String address, String telephoneNumber, String taxIdentificationNumber) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.gender = gender;
        this.city = city;
        this.address = address;
        this.telephoneNumber = telephoneNumber;
        this.taxIdentificationNumber = taxIdentificationNumber;
    }


}
