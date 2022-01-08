package com.ikoembe.study.schools;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends MongoRepository<School, String> {
    List<School> findByClassName(String className);
    Optional<School> findById(String id);

}
