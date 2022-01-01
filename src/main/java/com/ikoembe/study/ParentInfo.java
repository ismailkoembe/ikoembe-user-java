package com.ikoembe.study;

public class ParentInfo {
    private String fatherName;
    private String motherName;
    private Address address;

    public ParentInfo(String fatherName, String motherName, Address address) {
        this.fatherName = fatherName;
        this.motherName = motherName;
        this.address = address;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
