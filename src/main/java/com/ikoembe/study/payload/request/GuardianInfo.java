package com.ikoembe.study.payload.request;

import com.ikoembe.study.models.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class GuardianInfo {
    private String firstname;
    private String middlename;
    private String accountId;
    private String lastname;
    private String username;
    private Address address;
}
