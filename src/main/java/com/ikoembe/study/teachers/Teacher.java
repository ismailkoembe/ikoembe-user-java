package com.ikoembe.study.teachers;

import com.ikoembe.study.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Document(value = "teachers")
@AllArgsConstructor
@Getter
@Setter
public class Teacher {
    @Id
    private String id;
    @Indexed
    private String firstname;
    private String lastname;
    private Address address;
    private String email;
    private String photoUrl;
    @Indexed
    private String major;
    private String diplomaNumber;
    @Indexed
    private LocalDateTime appointedDate;
    private LocalDateTime startingDateOfEmployment;
    @Indexed
    private Boolean isClassTeacher;
    private Double weeklyWorkingHours;

}
