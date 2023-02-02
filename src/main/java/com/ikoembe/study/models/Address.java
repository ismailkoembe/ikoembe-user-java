package com.ikoembe.study.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Address {
    private String city;
    private String zipcode;
    private String country;
    private String street;
    private String number;
    private String phoneNumber;
    private String mobileNumber;
}
