package com.ikoembe.study;

import com.ikoembe.study.controller.AuthController;
import com.ikoembe.study.models.User;
import com.ikoembe.study.payload.request.LoginRequest;
import com.ikoembe.study.payload.response.JwtResponse;
import com.ikoembe.study.repository.UserRepository;
import com.ikoembe.study.security.jwt.JwtUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    @Test
    public void testAuthenticateUserSuccess() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin.admin");
        loginRequest.setPassword("Password11!");

        User user = new User();
        user.setUsername("admin.admin");
        user.setPassword("Password1!");

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(jwtUtils.generateJwtToken(any(Authentication.class))).thenReturn("testtoken");

        // Act
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals("testtoken", jwtResponse.getToken());
        assertEquals(user.getId(), jwtResponse.getId());

        // Verify that the user's lastSignIn was updated and userRepository.save() was called
        verify(userRepository, times(1)).save(user);
    }
}
