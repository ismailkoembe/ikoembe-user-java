package com.ikoembe.study.controller;

import com.ikoembe.study.models.Gender;
import com.ikoembe.study.payload.response.MessageResponse;
import com.ikoembe.study.payload.response.UserResponse;
import com.ikoembe.study.repository.RoleRepository;
import com.ikoembe.study.repository.UserRepository;
import com.ikoembe.study.security.jwt.JwtUtils;
import com.ikoembe.study.models.ERole;
import com.ikoembe.study.models.Role;
import com.ikoembe.study.models.User;
import com.ikoembe.study.service.UserImplementation;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.*;
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

    @Autowired
    UserImplementation userImplementation;


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
            return ResponseEntity.badRequest().body("Error: Role shouldn't be null");
        }
        if (strRoles.size()>1 && strRoles.stream().filter(r -> r.getName().name().equals("ROLE_STUDENT"))
                .collect(Collectors.toList())
                .size() == 1) {
            log.error("Students cannot have multiple roles");
            return ResponseEntity.badRequest().body("Error: Students cannot have multiple roles");
        }
        if (strRoles.size()>=1){
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


        user.setRoles(roles);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setCreatedDate(createdDate);
        userRepository.save(user);
        return ResponseEntity.ok(new UserResponse(
                user.getId(), user.getUsername(), user.getFirstname(),
                user.getMiddlename(), user.getLastname(), user.getEmail(),
                user.getRoles(), user.getBirthdate(), user.getGender(),
                user.getCreatedDate()

        ));
    }




    @GetMapping ("/byGender")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsersByGender(@Valid @RequestHeader Gender gender){
        List<User> byGender = userRepository.findAllByGender(gender);
        return byGender;
    }

    @GetMapping ("/role")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsersByRole(@Valid @RequestHeader String role){
        return userImplementation.findUserByRole(role);
    }

    @PatchMapping(path = "/update/username/{username}")
    @ApiOperation(value = "Patches a user's information with username")
    public ResponseEntity<User> patchStudentInfoBySchoolAccount(@PathVariable String username, @RequestBody Map<String, Object> patches){
        User user = userRepository.findByUsername(username);
        patches.forEach((k,v)-> {
            Field field = ReflectionUtils.findField(User.class, k);
            field.setAccessible(true);
            ReflectionUtils.setField(field,user, v);
        });
        this.userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @GetMapping ("/ByAge")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> findUserByAge(@Valid @RequestHeader int age, @RequestHeader String lastname){
        return userImplementation.findUserByAge(age, lastname);
    }


    @GetMapping("/bla")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User>findStudentByAge(int years){
        List<User> allStudents = getUsersByRole(ERole.ROLE_STUDENT.toString());
        List<User> eligibleStudents=  allStudents.stream().filter(user ->
            user.getBirthdate().isBefore(ChronoLocalDate.from(LocalDateTime.now()))
        ).collect(Collectors.toList());
        return eligibleStudents;
    }

}
