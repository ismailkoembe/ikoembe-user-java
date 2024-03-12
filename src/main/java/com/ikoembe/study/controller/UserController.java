package com.ikoembe.study.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikoembe.study.infra.UserCreatedMessage;
import com.ikoembe.study.infra.UserCreatedMessageFactory;
import com.ikoembe.study.models.Gender;
import com.ikoembe.study.models.Major;
import com.ikoembe.study.models.Roles;
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
import com.ikoembe.study.util.IsAuthenticated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import rabbitmq.RoutingKeys;

import javax.validation.Valid;
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

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;


    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new user", description = "Create a new user with the given details.")
    @ApiResponse(responseCode = "200", description = "User created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
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

        if (strRoles.size() > 1 && strRoles.stream().filter(r -> r.equals(Roles.ROLE_STUDENT))
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
                        isAbleToBeRegistered(user, isAdult, roles, role);
                        break;
                    case ROLE_STUDENT:
                        if (!userService.isOlderThan.apply(user.getBirthdate(), 18)) {
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
            publishUserCreatedMessage(user);

            if (user.isGuardianRequired()) {
                return ResponseEntity.status(201).body(createUserObject(user.getAccountId(), user.getFirstname(), user.getLastname(),
                        user.isGuardianRequired(), user.getMajors(), user.isTemporarilyPassword(), user.getRoles()));
            } else
                return ResponseEntity.ok(createUserObject(user.getAccountId(), user.getFirstname(), user.getLastname(),
                        user.isGuardianRequired(), user.getMajors(), user.isTemporarilyPassword(), user.getRoles()));
        } else
            return ResponseEntity.status(400).body(error.throwAnError("Admins, Teachers or Guardians should be older than 18"));
    }

    private void publishUserCreatedMessage(User user) {
        try {
            UserCreatedMessage userCreatedMessage = UserCreatedMessageFactory.newUserCreated(user);
            rabbitTemplate.convertAndSend("user", RoutingKeys.USER_CREATED.getRoutingKeyString(),
                    userCreatedMessage);
        } catch (IllegalStateException msg) {
            log.error("Failed to publish message:  {}", msg);
        }
    }

    private void isAbleToBeRegistered(User user, AtomicBoolean isAdult, Set<Roles> roles, Roles role) {
        if (userService.isOlderThan.apply(user.getBirthdate(), 18)) {
            roles.add(role);
        } else {
            isAdult.set(false);
            error.throwAnError("Admins, Teachers or Guardians should be older than 18");
        }
    }

    private UserResponse createUserObject(String accountId, String firstName, String lastName,
                                          boolean guardianRequired, List<Major> majors,
                                          boolean temporarilyPassword, Set<Roles>roles) {
        return new UserResponse(
                accountId,
                firstName,
                lastName,
                guardianRequired,
                majors,
                temporarilyPassword,roles );
    }


    @PostMapping("/addGuardian")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Adds guardians for student", description = "Client should call this endpoint if student is younger than 18")
    @ApiResponse(responseCode = "200", description = "Guardian added successfully",
            content = @Content(mediaType = "text/plain", examples = {
                    @ExampleObject(name = "GuardianAddedExample", value = "Guardian XYZ added for \nStudent ABC")
            }))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
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
    @Operation(summary = "Get all guardians information",
            description = "Client should call this API to get all guardian info thus guardian can be associated for student")
    @ApiResponse(responseCode = "200", description = "Guardians information retrieved successfully",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(value = "[{\"accountId\": \"guardian1\", \"guardianRequired\": true, \"createdDate\": \"2024-02-10T12:30:00\", \"temporarilyPassword\": false},{\"accountId\": \"guardian2\", \"guardianRequired\": false, \"createdDate\": \"2024-02-11T10:45:00\", \"temporarilyPassword\": true}]")
                    }))
    public ResponseEntity<?> getAllGuardians(@RequestHeader String role) {
        List<User> guardiansList = userService.findUserByRole(role);
        return ResponseEntity.ok().body(guardiansList.stream()
                .map(guardian -> createUserObject(guardian.getAccountId(),
                        guardian.getFirstname(), guardian.getLastname(),
                        guardian.isGuardianRequired(), guardian.getMajors(),
                        guardian.isTemporarilyPassword(), guardian.getRoles())));
    }

    @GetMapping("/byGender")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users by gender", description = "Gets all user by given genders")
    @ApiResponse(responseCode = "200", description = "Admin role is required",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<?> getUsersByGender(@Valid @RequestHeader Gender gender) {
        List<User> users = userRepository.findAllByGender(gender);
        return ResponseEntity.ok().body(users.stream().map(user -> createUserObject(user.getAccountId(),user.getFirstname(),
                user.getLastname(),
                user.isGuardianRequired(),
                user.getMajors(),
                user.isTemporarilyPassword(), user.getRoles())));
    }

    @GetMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users by role", description = "Gets all user by given role")
    @ApiResponse(responseCode = "200", description = "Admin role is required",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content, useReturnTypeSchema = true)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content, useReturnTypeSchema = true)
    public ResponseEntity<?> getUsersByRole(@Valid @RequestHeader String role) {
        List<User> userByRole = userService.findUserByRole(role);
        return ResponseEntity.ok().body(userByRole.stream()
                .map(user -> createUserObject(user.getAccountId(),
                        user.getFirstname(), user.getLastname(),
                        user.isGuardianRequired(),
                        user.getMajors(),
                        user.isTemporarilyPassword(),user.getRoles())));

    }

    @PatchMapping(path = "/update/username/{username}")
    @IsAuthenticated
    @GetMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Updates username", description = "Patches a user's information with username")
    @ApiResponse(responseCode = "200", description = "Admin role is required",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<?> patchUserInfo(
            @PathVariable String username, @RequestBody Map<String, Object> patches) {
        try {
            User user = userRepository.findByUsername(username);
            if (user != null) {
                Map<String, Object> validFields = patches.entrySet().stream()
                        .filter(x -> !x.getKey().equals("accountId") && !x.getKey().equals("password")
                                && !x.getKey().equals("roles") && !x.getKey().equals("createdDate")
                                && !x.getKey().equals("isGuardianRequired") && !x.getKey().equals("gender")
                                && !x.getKey().equals("adddress")
                        )
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                validFields.forEach((k, v) -> {
                    if (k.equals("address")) {log.warn("address can not be updated");}
                    Field field = ReflectionUtils.findField(User.class, k);
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, user, v);
                });
                this.userRepository.save(user);
                return ResponseEntity.ok(validFields);
            } else return ResponseEntity.badRequest().body("The user is not found");
        } catch (Exception e) {
            log.error("The patch field(s) is not valid");
            return ResponseEntity.badRequest().body("The patch field(s) is not valid");
        }

    }

    @PatchMapping(path = "/update/username/{username}/address")
    @IsAuthenticated
    @Operation(summary = "Updates user address", description = "Patches a user's address information by accountId")
    @ApiResponse(responseCode = "200", description = "Admin role is required",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<?> patchUserAddress(
            @PathVariable String username, @RequestBody User user,
            @RequestHeader String accountId){
        Optional<User> byAccountId = userRepository.findByAccountId(accountId);
        byAccountId.get().setAddress(user.getAddress());
        userService.upsert(accountId, byAccountId.get());
        return ResponseEntity.ok().body(byAccountId.stream().map(
                userX -> createUserObject(userX.getAccountId(),userX.getFirstname(), userX.getLastname(),
                        userX.isGuardianRequired(),
                        userX.getMajors(),
                        userX.isTemporarilyPassword(),user.getRoles())));
    }


    @GetMapping("/ByAgeAndRole")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users by age and role", description = "Return user by ages and roles")
    @ApiResponse(responseCode = "200", description = "Admin role is required",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<?> findUserByAge(
            @Valid @RequestHeader int age, @RequestHeader String role) {
        List<User> userByAgeAndRole = userService.findUserByAgeAndRole(age, role);
        return ResponseEntity.ok().body(userByAgeAndRole.stream()
                .map(userX -> createUserObject(userX.getAccountId(),userX.getFirstname(), userX.getLastname(),
                userX.isGuardianRequired(), userX.getMajors(), userX.isTemporarilyPassword(),userX.getRoles())
        ).collect(Collectors.toList()));

    }

    @GetMapping("/studentByAge")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get students by age", description = "Return students by age")
    @ApiResponse(responseCode = "200", description = "Admin role is required",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<?> findStudentByAge(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            int olderThan) {
        List<User> allStudents = userService.findUserByRole(Roles.ROLE_STUDENT.toString());
        List<User> eligibleStudents = allStudents.stream().filter(user ->
                user.getBirthdate().isBefore(ChronoLocalDate.from(LocalDateTime.now().minusYears(olderThan)))
        ).collect(Collectors.toList());
        return ResponseEntity.ok().body(eligibleStudents.stream().map(user ->
                createUserObject(user.getAccountId(), user.getFirstname(), user.getLastname(),
                        user.isGuardianRequired(), user.getMajors(), user.isTemporarilyPassword(),user.getRoles())
        ));
    }

    @GetMapping("/info")
    @Description("Admin users get single user details to provide user to first log in " +
            "thus user can get his temporary pass")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users details", description = "Returns user details by given accountId")
    @ApiResponse(responseCode = "200", description = "Admin role is required",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegistrationDetails.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<?> giveUserDetails(@Valid @RequestHeader String accountId) {
        Optional<User> user = userRepository.findByAccountId(accountId);
        if (user.isPresent()) {
            return ResponseEntity.ok(new RegistrationDetails(
                    user.get().getFirstname(),
                    user.get().getLastname(),
                    user.get().getMiddlename(),
                    user.get().getUsername(),
                    user.get().getTemporarilyPass()
            ));
        } else return ResponseEntity.badRequest().body("User is not found by accountId");

    }


    @PatchMapping(path = "/changePassword")
    @IsAuthenticated
    @Operation(summary = "Updates user password", description = "If user has temporary password, client should call force update user password")
    @ApiResponse(responseCode = "200", description = "Admin role is required",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<?> updatePassword(@RequestHeader String accountId, @RequestHeader String username,
                                            @RequestBody Map<String, String> newPassword) {
        try {
            User user = userRepository.findByAccountId(accountId, username);
            if (user.isTemporarilyPassword()) {
                log.info("User {}, {} attempts to change temporary password", user.getUsername(), user.getAccountId());
                if (encoder.matches(newPassword.get("currentPassword"), user.getPassword())) {
                    user.setPassword(encoder.encode(newPassword.get("newPassword")));
                    user.setTemporarilyPassword(false);
                    user.setTemporarilyPass(null);
                    userRepository.save(user);
                    return ResponseEntity.ok(
                            createUserObject(user.getAccountId(), user.getFirstname(), user.getLastname(),
                                    user.isGuardianRequired(), user.getMajors(), user.isTemporarilyPassword(),user.getRoles()));
                } else error.throwAnError("Current Password is not correct");
            } else {
                if (encoder.matches(newPassword.get("currentPassword"), user.getPassword())) {
                    log.info("User {}, {} attempts to change encrypted password", user.getUsername(), user.getAccountId());
                    log.info("Encrypted password matches user input");
                    user.setPassword(encoder.encode(newPassword.get("newPassword")));
                    user.setTemporarilyPassword(false);
                    user.setTemporarilyPass(null);
                    user.setLastPasswordUpdatedDate(LocalDateTime.now());
                    userRepository.save(user);
                    return ResponseEntity.ok(
                            createUserObject(user.getAccountId(), user.getFirstname(), user.getLastname(),
                                    user.isGuardianRequired(), user.getMajors(), user.isTemporarilyPassword()
                                    ,user.getRoles()));
                } else error.throwAnError("Current Password is not correct");

            }
            return ResponseEntity.badRequest().body("Current password doesn't match");


        } catch (Exception e) {
            log.error("User is not found");
        }
        return ResponseEntity.ok().body("User is not found");

    }

    @GetMapping("/isPasswordChangeRequired")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @IsAuthenticated
//    @ApiOperation(value = "")
    @Operation(summary = "Returns if password has to be changed", description = "Client should call this to understand if user should change password")
    @ApiResponse(responseCode = "200", description = "User should change its password",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "204", description = "Status 204 indicates password change is not required",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<?> isPasswordChangeRequired(@Valid @RequestHeader String accountId) {
        Optional<User> user = userRepository.findByAccountId(accountId);
        if (user.get().isTemporarilyPassword()) {
            return ResponseEntity.ok().body("Change the password");
        } else
            return ResponseEntity.status(204).build();
    }

    @GetMapping("/findByAccountId/{accountId}")
    @Operation(summary = "Get user by Id", description = "Gets a single user if account id is valid")
    @ApiResponse(responseCode = "200", description = "User is found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<Optional<User>> findByAccountId(@Valid @PathVariable String accountId) {
        Optional<User> user = userRepository.findByAccountId(accountId);
        log.info("User is found {}, {}", user.orElseThrow().getUsername(), user.orElseThrow().getAccountId());
        return ResponseEntity.ok().body(user);
    }

}
