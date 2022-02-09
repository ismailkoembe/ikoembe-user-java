package com.ikoembe.study.student;

import com.ikoembe.study.student.models.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//mongo repository class provides all relevant methods
@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    @Override
    Optional<Student> findById(String s);

    Optional<Student> findStudentBySchoolNumber(String email);

    @Override
    void deleteById(String id);

    @Override
    <S extends Student> S save(S entity);
}
