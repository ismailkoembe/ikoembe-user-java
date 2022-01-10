package com.ikoembe.study.student;

import com.ikoembe.study.Gender;
import com.ikoembe.study.Lessons;
import com.ikoembe.study.ParentInfo;
import com.ikoembe.study.teachers.Teacher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Document(value = "student")
@AllArgsConstructor
@Getter
@Setter
public class Student {
    @Id
    private String id;
    @Indexed(unique = true)
    private String schoolNumber;
    private String firstname;
    private String middleName;
    private String lastname;
    private String photoUrl;
    @Indexed
    private LocalDate birthdate;
    private Gender gender;
    @Indexed
    private String whichClass;
    @Indexed
    private String classRoomNumber;
    private String classTeacherName;
    private LocalDate enrollmentDayOfCurrentSchool;
    private LocalDate enrollmentDay;
    private LocalDateTime createdDate;
    private ParentInfo parentInfo;

}
