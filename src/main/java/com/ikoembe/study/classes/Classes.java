package com.ikoembe.study.classes;

import com.ikoembe.study.student.Student;
import com.ikoembe.study.teachers.Teacher;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(value = "Classes")
public class Classes {
    @Id
    private String id;
    @Indexed
    private String className;
    private Teacher classTeacher;

    public Classes(String id, String className, Teacher classTeacher) {
        this.id = id;
        this.className = className;
        this.classTeacher = classTeacher;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    public Teacher getClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(Teacher classTeacher) {
        this.classTeacher = classTeacher;
    }
}
