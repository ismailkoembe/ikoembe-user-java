package com.ikoembe.study.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mongodb.lang.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(value = "users")
@Getter @Setter @NoArgsConstructor @ToString
public class User {
  public static final String FIELD_ID = "id";
  public static final String FIELD_USERNAME = "username";
  public static final String FIELD_FIRSTNAME = "firstname";
  public static final String FIELD_MIDDLENAME = "middlename";
  public static final String FIELD_LASTNAME = "lastname";
  public static final String FIELD_EMAIL = "email";
  public static final String FIELD_PASSWORD = "password";
  public static final String FIELD_ROLES = "roles.name";
  public static final String FIELD_PHOTOURL = "photoUrl";
  public static final String FIELD_DOB = "birthdate";
  public static final String FIELD_GENDER = "gender";
  public static final String FIELD_CREATEDDATE = "createdDate";
  public static final String FIELD_LASTSIGNIN = "lastSignIn";


  @Id
  private String id;

  @NotNull
  @Size(max = 20)
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

  @NotBlank
  @Size(max = 120)
  private String password;

//  @DBRef
  private Set<Role> roles = new HashSet<>();

  private String photoUrl;

  @Indexed
  @NotNull
  @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate birthdate;

  @Indexed
  private Gender gender;

  @NotNull
  private LocalDateTime createdDate;

  @Nullable
  private LocalDateTime lastSignIn;


  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }
}
