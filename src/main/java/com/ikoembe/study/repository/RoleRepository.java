package com.ikoembe.study.repository;

import com.ikoembe.study.user.models.ERole;
import com.ikoembe.study.user.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
