package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j @Getter @Setter @AllArgsConstructor
public class User {

    private String accountId;

    private String username;

    private String firstname;

    private String middlename;

    private String lastname;

    private Set<Roles> roles = new HashSet<>();

    private LocalDate birthdate;


}
