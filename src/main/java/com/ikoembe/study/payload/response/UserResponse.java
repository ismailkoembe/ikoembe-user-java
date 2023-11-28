package com.ikoembe.study.payload.response;

import com.ikoembe.study.models.Major;
import com.ikoembe.study.models.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter @Setter @AllArgsConstructor
public class UserResponse {
    private String accountId;
    private String firstname;
    private String lastname;
    private boolean isGuardianRequired;
    private List<Major> majorList;
    private boolean isTemporarilyPassword;
    private Set<Roles> roles;

}
