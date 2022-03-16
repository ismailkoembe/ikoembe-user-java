package com.ikoembe.study.controller;

import com.ikoembe.study.models.Gender;
import com.ikoembe.study.payload.request.GuardianInfo;
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
import org.springframework.data.mongodb.repository.Query;
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
                            Role userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(userRole);
                            log.info("A new {} {} added", role.getName(), user.getUsername());
                            break;

                        case ROLE_STUDENT:
                            userRole = roleRepository.findByName(ERole.ROLE_STUDENT)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            if(user.getBirthdate().isBefore(ChronoLocalDate.from(LocalDateTime.now().minusYears(18)))){
                                if(user.getGuardianInfos()==null||user.getGuardianInfos().size()==0){
                                    log.error("Students who younger than 18 should be associated at least one guardian");
                                    //TODO : throw an error
                                    break;
                                }else {
                                    //TODO: Verify that guardian info is valid, if it is not handle NPE
                                    List<String>guardiansAccountId = new ArrayList<>();
                                    for (GuardianInfo guardianInfo : user.getGuardianInfos()) {
                                        guardiansAccountId.add(getGuardiansAccountIds(guardianInfo));
                                    }
                                    user.setGuardiansAccountIds(guardiansAccountId);
                                }

                            };

                            roles.add(userRole);
                            log.info("A new {} {} added", role.getName(), user.getUsername());
                            break;

                        case ROLE_GUARDIAN:
                            userRole = roleRepository.findByName(ERole.ROLE_GUARDIAN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(userRole);
                            log.info("A new {} {} added", role.getName(), user.getUsername());
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

        UUID uuid = UUID.randomUUID();
        user.setAccountId(uuid.toString());
        user.setRoles(roles);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setCreatedDate(createdDate);
        userRepository.save(user);
        return ResponseEntity.ok(new UserResponse(
                user.getAccountId(), user.getUsername(), user.getFirstname(),
                user.getMiddlename(), user.getLastname(), user.getEmail(),
                user.getRoles(), user.getBirthdate(), user.getGender(),
                user.getCreatedDate()

        ));
    }

    public String getGuardiansAccountIds(GuardianInfo guardianInfo){
        return userImplementation.getGuardianAccountId(guardianInfo);
    }


    @GetMapping ("/byGender")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsersByGender(@Valid @RequestHeader Gender gender){
        List<User> user = userRepository.findAllByGender(gender);
        List<UserResponse> userResponses = new ArrayList<>();
        for( User userX : user) {
            userResponses.add(new UserResponse(
                    userX.getAccountId(), userX.getUsername(), userX.getFirstname(),
                    userX.getMiddlename(), userX.getLastname(), userX.getEmail(),
                    userX.getRoles(), userX.getBirthdate(), userX.getGender(),
                    userX.getCreatedDate()));
        }
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping ("/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsersByRole(@Valid @RequestHeader String role){
        List<User> userByRole = userImplementation.findUserByRole(role);
        List<UserResponse> userResponses = new ArrayList<>();
        for( User userX : userByRole) {
            userResponses.add(new UserResponse(
                    userX.getAccountId(), userX.getUsername(), userX.getFirstname(),
                    userX.getMiddlename(), userX.getLastname(), userX.getEmail(),
                    userX.getRoles(), userX.getBirthdate(), userX.getGender(),
                    userX.getCreatedDate()));
        }
        return ResponseEntity.ok(userResponses);
    }

    @PatchMapping(path = "/update/username/{username}")
    @ApiOperation(value = "Patches a user's information with username")
    public ResponseEntity<?> patchStudentInfoBySchoolAccount(@PathVariable String username, @RequestBody Map<String, Object> patches){
        try {
            User user = userRepository.findByUsername(username);
            patches.forEach((k,v)-> {
                Field field = ReflectionUtils.findField(User.class, k);
                field.setAccessible(true);
                ReflectionUtils.setField(field,user, v);
            });
            this.userRepository.save(user);
            return ResponseEntity.ok(
                    new UserResponse(
                            user.getAccountId(), user.getUsername(), user.getFirstname(),
                            user.getMiddlename(), user.getLastname(), user.getEmail(),
                            user.getRoles(), user.getBirthdate(), user.getGender(),
                            user.getCreatedDate() )
            );
        }catch (Exception e){
            log.error("Username is not found");
        }
        return ResponseEntity.ok().body("Username is not exists in database");

    }

    @GetMapping ("/ByAgeAndRole")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findUserByAge(@Valid @RequestHeader int age, @RequestHeader String role) {
        List<User> userByAgeAndRole = userImplementation.findUserByAgeAndRole(age, role);
        List<UserResponse> userResponses = new ArrayList<>();
        for (User userX : userByAgeAndRole) {
            userResponses.add(new UserResponse(
                    userX.getAccountId(), userX.getUsername(), userX.getFirstname(),
                    userX.getMiddlename(), userX.getLastname(), userX.getEmail(),
                    userX.getRoles(), userX.getBirthdate(), userX.getGender(),
                    userX.getCreatedDate()));
        }
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/studentByAge")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?>findStudentByAge(int olderThan) {
        List<User> allStudents = userImplementation.findUserByRole(ERole.ROLE_STUDENT.toString());
        List<User> eligibleStudents = allStudents.stream().filter(user ->
                user.getBirthdate().isBefore(ChronoLocalDate.from(LocalDateTime.now().minusYears(olderThan)))
        ).collect(Collectors.toList());
        List<UserResponse> userResponses = new ArrayList<>();
        for (User userX : eligibleStudents) {
            userResponses.add(new UserResponse(
                    userX.getAccountId(), userX.getUsername(), userX.getFirstname(),
                    userX.getMiddlename(), userX.getLastname(), userX.getEmail(),
                    userX.getRoles(), userX.getBirthdate(), userX.getGender(),
                    userX.getCreatedDate()));
        }
        return ResponseEntity.ok(userResponses);
    }

}
