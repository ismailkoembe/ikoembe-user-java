package com.ikoembe.study.controller;

import com.ikoembe.study.models.Roles;
import com.ikoembe.study.models.Gender;
import com.ikoembe.study.models.User;
import com.ikoembe.study.payload.request.GuardianInfo;
import com.ikoembe.study.payload.response.MessageResponse;
import com.ikoembe.study.payload.response.RegistrationDetails;
import com.ikoembe.study.payload.response.UserResponse;
import com.ikoembe.study.repository.UserRepository;
import com.ikoembe.study.security.jwt.JwtUtils;
import com.ikoembe.study.security.services.UserDetailsImpl;
import com.ikoembe.study.service.UserService;
import com.ikoembe.study.util.ErrorResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @Autowired
    ErrorResponse error;


    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        String temporaryPassword = RandomStringUtils.random(12, true, true);
        log.info("temporaryPassword: " + temporaryPassword);
        LocalDateTime createdDate = LocalDateTime.now();
        AtomicBoolean isAdult = new AtomicBoolean(true);
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        Set<Roles> strRoles = user.getRoles();
        Set<Roles> roles = new HashSet<>();

        if (strRoles.size() == 0) {
            log.error("Role shouldn't be null");
            return ResponseEntity.badRequest().body("Error: Role shouldn't be null");
        }

        if (strRoles.size() > 1 && strRoles.stream().filter(r -> r.equals(Roles.ROLE_STUDENT.getName()))
                .collect(Collectors.toList())
                .size() >= 1) {
            log.error("Students cannot have multiple roles");

            return ResponseEntity.badRequest().body("Error: Students cannot have multiple roles");
        }

        if (strRoles.size() >= 1) {
            strRoles.forEach(role -> {
                switch (role) {
                    case ROLE_ADMIN:
                    case ROLE_TEACHER:
                    case ROLE_GUARDIAN:
                        if (userService.isUserOlderThan(user.getBirthdate(), 18)) {
                            roles.add(role);
                        } else {
                            isAdult.set(false);
                            error.throwAnError("Admins, Teachers or Guardians should be older than 18");
                        }
                        break;
                    case ROLE_STUDENT:
                        if (!userService.isUserOlderThan(user.getBirthdate(), 18)) {
                            user.setGuardianRequired(true);
                        }
                        roles.add(role);
                        break;
                    default:
                        log.error("Error: Role {} is not found", role);
                        throw new RuntimeException("Error: Role is not found");
                }
            });
        }
        if (isAdult.get()) {
            UUID uuid = UUID.randomUUID();
            user.setAccountId(uuid.toString());
            user.setRoles(roles);
            user.setPassword(encoder.encode(temporaryPassword));
            user.setTemporarilyPass(temporaryPassword);
            user.setCreatedDate(createdDate);
            userRepository.save(user);
            log.info("A new {} {} added", user.getRoles(), user.getUsername());

            if (user.isGuardianRequired()) {
                return ResponseEntity.status(201).body(createUserObject(user.getAccountId(), user.isGuardianRequired(), user.getCreatedDate(), user.isTemporarilyPassword()));
            } else
                return ResponseEntity.ok(createUserObject(user.getAccountId(), user.isGuardianRequired(), user.getCreatedDate(), user.isTemporarilyPassword()));
        } else
            return ResponseEntity.status(400).body(error.throwAnError("Admins, Teachers or Guardians should be older than 18"));
    }

    private UserResponse createUserObject(String accountId, boolean guardianRequired, LocalDateTime createdDate, boolean temporarilyPassword) {
        return new UserResponse(
                accountId,
                guardianRequired,
                createdDate,
                temporarilyPassword);
    }

    @PostMapping("/addGuardian")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation("Client should call this endpoint if student is younger than 18")
    public ResponseEntity<?> addGuardian(@Valid @RequestBody List<GuardianInfo> guardianInfo,
                                         @RequestHeader String studentAccountId) {
        List<String> guardiansAccountId;
        User student = userRepository.findByAccountId(studentAccountId).orElseThrow(
                () -> new RuntimeException("Student is not found in database"));
        guardiansAccountId = guardianInfo.stream().map(guardianInfos -> userRepository.findByAccountId(guardianInfos.getAccountId())
                .orElseThrow(() -> new RuntimeException("Guardian is not found"))).map(User::getAccountId).collect(Collectors.toList());
        log.info("{} added for student {}", guardiansAccountId, student.getAccountId());
        student.setGuardiansAccountIds(guardiansAccountId);
        userRepository.save(student);
        return ResponseEntity.ok(guardiansAccountId + "added for /n" + student.getAccountId());
    }


    @GetMapping("/allGuardians")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation("Client should call this api to get all guardian info thus guardian can be associated for student")
    public ResponseEntity<?> getAllGuardians(@RequestHeader String role) {
        List<User> guardiansList = userService.findUserByRole(role);
        return ResponseEntity.ok().body(guardiansList.stream().map(g -> createUserObject(g.getAccountId(), g.isGuardianRequired(), g.getCreatedDate(), g.isTemporarilyPassword())));
    }

    @GetMapping("/byGender")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsersByGender(@Valid @RequestHeader Gender gender) {
        List<User> user = userRepository.findAllByGender(gender);
        return ResponseEntity.ok().body(user.stream().map(u -> createUserObject(u.getAccountId(), u.isGuardianRequired(), u.getCreatedDate(), u.isTemporarilyPassword())));
    }

    @GetMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsersByRole(@Valid @RequestHeader String role) {
        List<User> userByRole = userService.findUserByRole(role);
        return ResponseEntity.ok().body(userByRole.stream().map(userX -> createUserObject(userX.getAccountId(), userX.isGuardianRequired(), userX.getCreatedDate(), userX.isTemporarilyPassword())));

    }

    @PatchMapping(path = "/update/username/{username}")
    @ApiOperation(value = "Patches a user's information with username")
    public ResponseEntity<?> patchStudentInfoBySchoolAccount(
            @PathVariable String username, @RequestBody Map<String, Object> patches) {
        try {
            User user = userRepository.findByUsername(username);
            patches.forEach((k, v) -> {
                Field field = ReflectionUtils.findField(User.class, k);
                field.setAccessible(true);
                ReflectionUtils.setField(field, user, v);
            });
            this.userRepository.save(user);
            return ResponseEntity.ok(
                    createUserObject(user.getAccountId(), user.isGuardianRequired(), user.getCreatedDate(), user.isTemporarilyPassword()));
        } catch (Exception e) {
            log.error("Username is not found");
        }
        return ResponseEntity.ok().body("Username is not exists in database");

    }

    @GetMapping("/ByAgeAndRole")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findUserByAge(
            @Valid @RequestHeader int age, @RequestHeader String role) {
        List<User> userByAgeAndRole = userService.findUserByAgeAndRole(age, role);
        List<UserResponse> userResponses = new ArrayList<>();
        return ResponseEntity.ok().body(userResponses.stream().map(userX -> createUserObject(userX.getAccountId(), userX.isGuardianRequired(), userX.getCreatedDate(), userX.isTemporarilyPassword())
        ));

    }

    @GetMapping("/studentByAge")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findStudentByAge(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            int olderThan) {
        List<User> allStudents = userService.findUserByRole(Roles.ROLE_STUDENT.toString());
        List<User> eligibleStudents = allStudents.stream().filter(user ->
                user.getBirthdate().isBefore(ChronoLocalDate.from(LocalDateTime.now().minusYears(olderThan)))
        ).collect(Collectors.toList());
        return ResponseEntity.ok().body(eligibleStudents.stream().map(e -> createUserObject(e.getAccountId(), e.isGuardianRequired(), e.getCreatedDate(), e.isTemporarilyPassword())
        ));
    }

    @GetMapping("/info")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> giveUserDetails(@Valid @RequestHeader String accountId) {
        Optional<User> user = userRepository.findByAccountId(accountId);
        return ResponseEntity.ok(new RegistrationDetails(
                user.get().getFirstname(),
                user.get().getLastname(),
                user.get().getMiddlename(),
                user.get().getUsername(),
                user.get().getTemporarilyPass()
        ));
    }


    @PatchMapping(path = "/changePassword")
    @ApiOperation(value = "If user has temporary password, client should call force update user password")
    public ResponseEntity<?> updatePassword(@RequestHeader String accountId, @RequestHeader String username,
                                            @RequestBody Map<String, String> newPassword) {
        try {
            User user = userRepository.findByAccountId(accountId, username);
            if (user.isTemporarilyPassword()) {
                if (encoder.matches(newPassword.get("currentPassword"), user.getPassword())) {
                    user.setPassword(encoder.encode(newPassword.get("newPassword")));
                    user.setTemporarilyPassword(false);
                    userRepository.save(user);
                    return ResponseEntity.ok(
                            createUserObject(user.getAccountId(), user.isGuardianRequired(), user.getCreatedDate(), user.isTemporarilyPassword()));
                } else error.throwAnError("Current Password is not correct");
            } else {
                if (encoder.matches(newPassword.get("currentPassword"), user.getPassword())) {
                    log.info("Encrypted password matches user input");
                    user.setPassword(encoder.encode(newPassword.get("newPassword")));
                    user.setTemporarilyPassword(false);
                    user.setTemporarilyPass(null);
                    user.setLastPasswordUpdatedDate(LocalDateTime.now());
                    userRepository.save(user);
                    return ResponseEntity.ok(
                            createUserObject(user.getAccountId(), user.isGuardianRequired(), user.getCreatedDate(), user.isTemporarilyPassword()));
                } else error.throwAnError("Current Password is not correct");

            }
            return ResponseEntity.badRequest().body("Current password doesn't match");


        } catch (Exception e) {
            log.error("User is not found");
        }
        return ResponseEntity.ok().body("User is not found");

    }

    @GetMapping("/isPasswordChangeRequired")
    @ApiOperation(value = "Client should call this to understand if user should change password")
    public ResponseEntity<?> isPasswordChangeRequired(@Valid @RequestHeader String accountId) {
        Optional<User> user = userRepository.findByAccountId(accountId);
        if (user.get().isTemporarilyPassword()) {
            return ResponseEntity.ok().body("Change the password");
        } else
            return ResponseEntity.status(204).build();
    }

    @GetMapping("/findByAccountId/{accountId}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Optional<User>> doSomething(@Valid @PathVariable String accountId) {
        Optional<User> user = userRepository.findByAccountId(accountId);
        return ResponseEntity.ok().body(user);
    }

}
