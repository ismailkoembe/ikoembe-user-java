package com.ikoembe.study.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor
public class UserResponse {
    private String accountId;
    private boolean isGuardianRequired;
    private LocalDateTime createdDate;
    private boolean isTemporarilyPassword;

}
