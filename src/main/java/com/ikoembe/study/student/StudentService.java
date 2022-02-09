package com.ikoembe.study.student;

import com.ikoembe.study.student.models.Student;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Optional<Student> findStudentBySchoolNumber(String schoolNumber) {
        Optional<Student>student = this.studentRepository.findStudentBySchoolNumber(schoolNumber);
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
