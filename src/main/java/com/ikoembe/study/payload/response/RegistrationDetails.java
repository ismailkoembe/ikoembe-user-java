package com.ikoembe.study.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter @AllArgsConstructor
public class RegistrationDetails {
    private String firstname;
    private String lastname;
    private String middlename;
    private String username;
    private String password;
}
