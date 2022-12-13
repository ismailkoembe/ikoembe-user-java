package model;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


public class User {
    private String accountId;

    private String username;

    private String firstname;

    private String middlename;

    private String lastname;

    private Set<Roles> roles = new HashSet<>();

    private LocalDate birthdate;

    public User() {
    }

    public User(String accountId, String username, String firstname, String middlename, String lastname, Set<Roles> roles, LocalDate birthdate) {
        this.accountId = accountId;
        this.username = username;
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.roles = roles;
        this.birthdate = birthdate;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Set<Roles> getRoles() {
        return roles;
    }

    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }
}
