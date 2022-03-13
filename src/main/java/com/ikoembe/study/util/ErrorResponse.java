package com.ikoembe.study.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class ErrorResponse {
    private String errorMessage;

    public void throwAnError() {
        throwAnError();
    }

    public void throwAnError(String errorMessage){
        new RuntimeException(errorMessage);
    }

}
