package com.ikoembe.study.student;

import com.ikoembe.study.Gender;
import com.ikoembe.study.Lessons;
import com.ikoembe.study.ParentInfo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(value = "Student")
public class Student {
    @Id
    private String id;
    private String firstname;
    private String lastname;
    @Indexed(unique = true)
    private String email;
    private Gender gender;
    private List<String> hobbies;
    private BigDecimal schoolAccount;
    private LocalDateTime createdDate;
    private List<Lessons> lessons;
    private ParentInfo parentInfo;

    public Student(String id, String firstname, String lastname, String email, Gender gender, List<String> hobbies, BigDecimal schoolAccount, LocalDateTime createdDate, List<Lessons> lessons, ParentInfo parentInfo) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.gender = gender;
        this.hobbies = hobbies;
        this.schoolAccount = schoolAccount;
        this.createdDate = createdDate;
        this.lessons = lessons;
        this.parentInfo = parentInfo;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    public BigDecimal getSchoolAccount() {
        return schoolAccount;
    }

    public void setSchoolAccount(BigDecimal schoolAccount) {
        this.schoolAccount = schoolAccount;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public List<Lessons> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lessons> lessons) {
        this.lessons = lessons;
    }

    public ParentInfo getParentInfo() {
        return parentInfo;
    }

    public void setParentInfo(ParentInfo parentInfo) {
        this.parentInfo = parentInfo;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", gender=" + gender +
                ", favoriteSubjects=" + hobbies +
                ", schoolAccount=" + schoolAccount +
                ", createdDate=" + createdDate +
                ", lessons=" + lessons +
                ", parentInfo=" + parentInfo +
                '}';
    }
}
