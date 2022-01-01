package com.ikoembe.study.classes;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends MongoRepository<Classes, String> {
    List<Classes> findByClassName(String className);
    Optional<Classes> findById(String id);

}
