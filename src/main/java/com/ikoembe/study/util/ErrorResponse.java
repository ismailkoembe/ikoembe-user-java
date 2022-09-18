package com.ikoembe.study.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@Getter @Setter
public class ErrorResponse {
    private String errorMessage;

    public void throwAnError() {

    }

    @GetMapping("/error")
    public ResponseEntity<String> throwAnError(@RequestBody String errorMessage){
        return ResponseEntity.badRequest().body(errorMessage);
    }


}
