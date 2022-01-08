package com.ikoembe.study;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Address {
    private String city;
    private String country;
    private String street;
    private String phoneNumber;
}
