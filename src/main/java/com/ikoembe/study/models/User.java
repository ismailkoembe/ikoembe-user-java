package com.ikoembe.study.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.lang.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import model.Majors;
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
@Getter @Setter @NoArgsConstructor
public class User {
  public static final String FIELD_ID = "id";
  public static final String FIELD_ACCOUNTID = "accountId";
  public static final String FIELD_USERNAME = "username";
  public static final String FIELD_FIRSTNAME = "firstname";
  public static final String FIELD_MIDDLENAME = "middlename";
  public static final String FIELD_LASTNAME = "lastname";
  public static final String FIELD_EMAIL = "email";
  public static final String FIELD_PASSWORD = "password";
  public static final String FIELD_ROLES = "roles";
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
  public static final String FIELD_ADDRESS_CITY = "address.city";
  public static final String FIELD_ADDRESS_ZIPCODE = "address.zipcode";
  public static final String FIELD_ADDRESS_COUNTRY = "address.country";
  public static final String FIELD_ADDRESS_STREET = "address.street";
  public static final String FIELD_ADDRESS_NUMBER = "address.number";
  public static final String FIELD_ADDRESS_PHONENUMBER = "address.phoneNumber";
  public static final String FIELD_ADDRESS_MOBILENUMBER = "address.mobileNumber";



  @Id
  private String id;

  @Indexed(unique = true)
  @JsonProperty
  private String accountId;

  @NotNull
  @Indexed(unique = true)
  @Size(max = 20)
  @JsonProperty
  private String username;

  @NotBlank
  @Size(max = 20)
  @JsonProperty
  private String firstname;

  @Nullable
  @Size(max = 20)
  @JsonProperty
  private String middlename;

  @NotBlank
  @Size(max = 20)
  @JsonProperty
  private String lastname;

  @NotBlank
  @Size(max = 50)
  @Email
  @JsonProperty
  private String email;

  @NotBlank(message = "Password shouldn't be blank")
  @Size(max = 120)
  private String password;

  @Indexed
  @JsonProperty
  private Set<Roles> roles = new HashSet<>();

  @JsonProperty
  private String photoUrl;

  @JsonProperty
  private boolean isGuardianRequired = false;

  @Nullable
  @JsonProperty
  private List<String> guardiansAccountIds;

  @Indexed
  @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @JsonProperty
  private LocalDate birthdate;

  @Indexed
  @JsonProperty
  private Gender gender;

  @NotBlank
  @JsonProperty
  private Address address;

  @NotNull
  @JsonProperty
  private LocalDateTime createdDate;

  @Indexed
  @Nullable
  @JsonProperty
  private LocalDateTime lastSignIn;

  @Nullable
  @JsonProperty
  private boolean isTemporarilyPassword = true;

  private String temporarilyPass;

  @JsonProperty
  private LocalDateTime lastPasswordUpdatedDate;

  @JsonProperty
  private Set<Majors> majors;


  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public User(String accountId, String username, String password, Set<Roles> roles) {
    this.accountId = accountId;
    this.username = username;
    this.password = password;
    this.roles = roles;
  }

  @Override
  public String toString() {
    return "User{" +
            "accountId='" + accountId + '\'' +
            ", username='" + username + '\'' +
            ", firstname='" + firstname + '\'' +
            ", middlename='" + middlename + '\'' +
            ", lastname='" + lastname + '\'' +
            ", roles=" + roles +
            ", photoUrl='" + photoUrl + '\'' +
            ", isGuardianRequired=" + isGuardianRequired +
            ", guardiansAccountIds=" + guardiansAccountIds +
            ", birthdate=" + birthdate +
            ", gender=" + gender +
            ", address=" + address +
            ", isTemporarilyPassword=" + isTemporarilyPassword +
            ", temporarilyPass='" + temporarilyPass + '\'' +
            ", majors=" + majors +
            '}';
  }
}
