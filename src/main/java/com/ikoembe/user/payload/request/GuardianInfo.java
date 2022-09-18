package com.ikoembe.user.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class GuardianInfo {
    private String accountId;
    private String username;
}
