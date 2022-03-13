package com.ikoembe.study.repository;

import com.ikoembe.study.models.ERole;
import com.ikoembe.study.models.Gender;
import com.ikoembe.study.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    User findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByRoles(ERole role);

    Boolean existsByEmail(String email);

    Boolean existsByFirstname(String firstname);

    Boolean existsByLastname(String lastname);

    List<User> findAllByGender(Gender gender);

    List<User> findAllByRoles(ERole role);

    List<User> findAllByLastname(String lastname);

}
