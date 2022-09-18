package com.ikoembe.user.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class RegistrationDetails {
    private String firstname;
    private String lastname;
    private String middlename;
    private String username;
    private String temporarilyPass;
}
