package com.ikoembe.study.teachers;

import com.ikoembe.study.teachers.Teacher;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeachersRepository extends MongoRepository<Teacher, String> {
    @Override
    <S extends Teacher> S insert(S entity);
}
