package com.ikoembe.study.infra;

import com.ikoembe.study.models.User;

import java.time.LocalDateTime;

public class UserCreatedMessageFactory {

    public static UserCreatedMessage newUserCreated(User user){
        return new UserCreatedMessage(
                user.getAccountId(),
                user.getUsername(),
                user.getFirstname(),
                user.getMiddlename(),
                user.getLastname(),
                user.getEmail(),
                user.getRoles(),
                user.getPhotoUrl(),
                user.getGuardiansAccountIds(),
//                user.getBirthdate(),
                user.getGender(),
//                LocalDateTime.now(),
                user.getMajors()
        );

    }

}
