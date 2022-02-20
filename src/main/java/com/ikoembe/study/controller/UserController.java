package com.ikoembe.study.controller;

import com.ikoembe.study.Gender;
import com.ikoembe.study.payload.response.MessageResponse;
import com.ikoembe.study.repository.RoleRepository;
import com.ikoembe.study.repository.UserRepository;
import com.ikoembe.study.security.jwt.JwtUtils;
import com.ikoembe.study.user.models.ERole;
import com.ikoembe.study.user.models.Role;
import com.ikoembe.study.user.models.User;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.web.servlet.headers.HeadersSecurityMarker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        LocalDateTime createdDate = LocalDateTime.now();
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (user.getEmail()!=null && userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        log.info("A new user object created {} {}",user.getUsername(), user.getRoles());

        Set<Role> strRoles = user.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles.size()==0) {
            log.error("Role shouldn't be null");
            throw new RuntimeException("Error: Role shouldn't be null");
        }
        if (strRoles.size()>=1 ) {
            if (strRoles.stream().filter(r -> r.getName().name().equals("ROLE_STUDENT"))
                    .collect(Collectors.toList())
                    .size()==1) {
                log.error("Students cannot have multiple roles");
                throw new RuntimeException("Error: Students cannot have multiple roles");
            } else {
                strRoles.forEach(role -> {
                    switch (role.getName()) {
                        case ROLE_ADMIN:
                            log.info("A new {} {} added", role.getName(), user.getUsername());
                            Role userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(userRole);
                            break;

                        case ROLE_STUDENT:
                            log.info("A new {} {} added", role.getName(), user.getUsername());
                            userRole = roleRepository.findByName(ERole.ROLE_STUDENT)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(userRole);
                            break;

                        case ROLE_GUARDIAN:
                            log.info("A new {} {} added", role.getName(), user.getUsername());
                            userRole = roleRepository.findByName(ERole.ROLE_GUARDIAN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(userRole);
                            break;

                        case ROLE_TEACHER:
                            log.info("A new {} {} added", role.getName(), user.getUsername());
                            userRole = roleRepository.findByName(ERole.ROLE_TEACHER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(userRole);
                            break;

                        default:
                            log.error("Error: Role {} is not found", role.getName());
                            throw new RuntimeException("Error: Role is not found");
                    }
                });
            }
        }

        user.setRoles(roles);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setCreatedDate(createdDate);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }



    @GetMapping ("/gender")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<User> getUsersByGender(@Valid @RequestHeader Gender gender){
        Optional<User> byGender = userRepository.findByGender(gender);
        return byGender;
    }

}
