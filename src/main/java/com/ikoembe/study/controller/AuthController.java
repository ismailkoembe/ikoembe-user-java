package com.ikoembe.study.controller;

import com.ikoembe.study.infra.RabbitConfiguration;
import com.ikoembe.study.models.Roles;
import com.ikoembe.study.models.User;
import com.ikoembe.study.payload.request.LoginRequest;
import com.ikoembe.study.payload.request.SignupRequest;
import com.ikoembe.study.payload.response.JwtResponse;
import com.ikoembe.study.payload.response.MessageResponse;
import com.ikoembe.study.repository.UserRepository;
import com.ikoembe.study.security.jwt.JwtUtils;
import com.ikoembe.study.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rabbitmq.RoutingKeys;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;


    @Autowired
    private RabbitTemplate template;


    @PostMapping("/signin")
    @Operation(summary = "Signin", description = "User can get logged in and gets Bearer Token ")
    @ApiResponse(responseCode = "200", description = "User can login",
            content = @Content(mediaType = "application/json", schema= @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "400", description = "Bad call", content = @Content)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        LocalDateTime lastSignIn = LocalDateTime.now();
        log.info("Login request for {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (authentication.isAuthenticated()) {
            user.setLastSignIn(lastSignIn);
            userRepository.save(user);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId()));
    }

    @Deprecated
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (signUpRequest.getEmail() != null && userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        log.info("A new user object created {} {}", signUpRequest.getUsername(), signUpRequest.getRoles());
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));


        Set<Roles> strRoles = signUpRequest.getRoles();
        Set<Roles> roles = new HashSet<>();

        if (strRoles == null) {
            log.error("Role shouldn't be null");
            throw new RuntimeException("Error: Role shouldn't be null");
        } else {
            strRoles.forEach(role -> {
                        log.info("A new {} {} added", signUpRequest.getRoles(), signUpRequest.getUsername());
                        roles.add(role);
                }
            );
        }

        user.setRoles(roles);
        userRepository.save(user);
//        rabbitClient.sendMessage(RoutingKeys.USER_SIGNED_IN, signUpRequest.getUsername());
//        template.convertAndSend(RabbitConfiguration.EXCHANGE,
//                RabbitConfiguration.ROUTING_KEY, signUpRequest.toString());
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

}
