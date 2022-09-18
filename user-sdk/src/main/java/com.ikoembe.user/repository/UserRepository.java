package com.ikoembe.user.repository;

import com.ikoembe.user.models.ERole;
import com.ikoembe.user.models.Gender;
import com.ikoembe.user.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    Optional<User> findByAccountId(String accountId);

    User findByAccountId(String accountId, String username);

    Optional<List<String>> findAllByAccountId(List<String> accountIds);

}
