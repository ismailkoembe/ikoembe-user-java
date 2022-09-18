package com.ikoembe.user.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mongodb.lang.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(value = "users")
@Getter @Setter @NoArgsConstructor @ToString
public class User {
  public static final String FIELD_ID = "id";
  public static final String FIELD_ACCOUNTID = "accountId";
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
  public static final String FIELD_GUARDIANS = "guardians";
  public static final String FIELD_GUARDIANINFO = "guardian";
  public static final String FIELD_GUARDIAN_FIRSTNAME = "guardian.firstname";
  public static final String FIELD_GUARDIAN_LASTNAME = "guardian.lastname";
  public static final String FIELD_GUARDIAN_MIDDLENAME = "guardian.middlename";
  public static final String FIELD_GUARDIAN_USERNAME = "guardian.username";
  public static final String FIELD_GUARDIAN_ADDRESS = "guardian.address";
  public static final String FIELD_CREATEDDATE = "createdDate";
  public static final String FIELD_LASTSIGNIN = "lastSignIn";
  public static final String FIELD_ISTEMPORARYPASSWORD = "isTemporarilyPassword";
  public static final String FIELD_TEMPORARYPASSWORD = "temporarilyPassword";



  @Id
  private String id;

  @Indexed(unique = true)
  private String accountId;

  @NotNull
  @Indexed(unique = true)
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

  @NotBlank(message = "Password shouldn't be blank")
  @Size(max = 120)
  private String password;

//  @DBRef
  private Set<Role> roles = new HashSet<>();

  private String photoUrl;

  private boolean isGuardianRequired = false;

  @Nullable
  private List<String> guardiansAccountIds;

  @Indexed
  @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate birthdate;

  @Indexed
  private Gender gender;

  @NotBlank
  private Address address;

  @NotNull
  private LocalDateTime createdDate;

  @Indexed
  @Nullable
  private LocalDateTime lastSignIn;

  @Nullable
  private boolean isTemporarilyPassword = true;

  private String temporarilyPass;

  private LocalDateTime lastPasswordUpdatedDate;


  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public User(String accountId, String username, String password, Set<Role> roles) {
    this.accountId = accountId;
    this.username = username;
    this.password = password;
    this.roles = roles;
  }
}
