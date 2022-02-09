package com.ikoembe.study.student.models;

import com.ikoembe.study.Gender;
import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(value = "student")
@AllArgsConstructor
@Getter
@Setter
public class Student {
    @Id
    private String id;
    @Nullable
    private String schoolNumber;
    @NotNull
    private String firstname;
    @NotNull
    private String middleName;
    private String lastname;
    private String photoUrl;
    @Indexed
    @NotNull
    private LocalDate birthdate;
    @NotNull
    private String role;
    private Gender gender;
    private LocalDateTime createdDate;
}
