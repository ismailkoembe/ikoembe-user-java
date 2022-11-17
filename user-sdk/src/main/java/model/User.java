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
}
