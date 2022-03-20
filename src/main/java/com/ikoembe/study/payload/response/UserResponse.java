package com.ikoembe.study.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ikoembe.study.models.Gender;
import com.ikoembe.study.models.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter @AllArgsConstructor
public class UserResponse {
    private String accountId;
    private boolean isGuardianRequired;
    private LocalDateTime createdDate;

}
