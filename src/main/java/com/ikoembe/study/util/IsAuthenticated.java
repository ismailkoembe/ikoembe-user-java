package com.ikoembe.study.util;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ROLE_ADMIN') " +
        "|| hasRole('ROLE_TEACHER')" +
        "|| hasRole('ROLE_GUARDIAN')" +
        "|| (hasRole('ROLE_STUDENT') && #userId == principal.username)")
public @interface IsAuthenticated {
}
