package com.ikoembe.study.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ikoembe.study.models.Address;
import com.ikoembe.study.models.Gender;
import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter @Setter @AllArgsConstructor
public class UserRequest {


    private String username;

    @NotBlank
    @Size(max = 20)
    private String firstname;

    @Nullable
    @Size(max = 20)
    private String middlename;

    @NotBlank
    @Size(max = 20)
    private String lastname;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank(message = "Password shouldn't be blank")
    @Size(max = 120)
    private String password;

    private Set<String> roles = new HashSet<>();

    @Nullable
    private List<String> guardiansAccountIds;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @Indexed
    private Gender gender;

    @NotBlank
    private Address address;

}
