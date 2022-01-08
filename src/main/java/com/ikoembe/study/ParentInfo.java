package com.ikoembe.study;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ParentInfo {
    private String fatherName;
    private String motherName;
    private Address address;

}
