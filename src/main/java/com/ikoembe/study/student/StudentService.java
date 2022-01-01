package com.ikoembe.study.student;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ikoembe.study.student.Student;
import com.ikoembe.study.student.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;


    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    public void insert(Student student) {
        this.studentRepository.insert(student);
    }

    public List<Student> findAll() {
        List<Student> students = this.studentRepository.findAll();
        return students;
    }

    public Optional<Student> findStudentByEmail(String email) {
        Optional<Student>student = this.studentRepository.findStudentByEmail(email);
        return student;
    }

    public Optional<Student> findStudentById(String id) {
        Optional<Student>student = this.studentRepository.findById(id);
        return student;
    }

    public void deleteStudentById(String id){
        this.studentRepository.deleteById(id);
    }

    public void updateStudentInfo(Student student) {
        this.studentRepository.save(student);

    }
}
