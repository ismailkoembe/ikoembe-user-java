package com.ikoembe.study.schools;

import com.ikoembe.study.student.Student;
import com.ikoembe.study.teachers.Teacher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(value = "schools")
@AllArgsConstructor
@Getter
@Setter
public class School {
    @Id
    private String id;
    @Indexed
    private String schoolName;
    private List<String> classes;
    private List<Teacher> teachers;
    private List<Student> students;

}
