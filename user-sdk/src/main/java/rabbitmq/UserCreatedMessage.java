package rabbitmq;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import model.Gender;
import model.Majors;
import model.Roles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCreatedMessage {
    private String accountId;
    private String username;
    private String firstname;
    private String middlename;
    private String lastname;
    private String email;
    private Set<Roles> roles = new HashSet<>();
    private String photoUrl;
    private List<String> guardiansAccountIds;
//    private LocalDate birthdate;
    private Gender gender;
//    private LocalDateTime createdDate;
    private Set<Majors> majors;

}
