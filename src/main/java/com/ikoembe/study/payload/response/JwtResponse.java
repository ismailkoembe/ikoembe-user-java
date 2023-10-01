package com.ikoembe.study.payload.response;

import com.ikoembe.study.models.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter @Setter
public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String id;
	private String accountId;
	private String photoUrl;
	private String firstname;
	private String lastname;
	private String middleName;
	private Set<Roles> userRole;

	public JwtResponse(String token, String id, String accountId, String photoUrl, String firstname,
					   String lastname, String middleName, Set<Roles> userRole) {
		this.token = token;
		this.id = id;
		this.accountId = accountId;
		this.photoUrl = photoUrl;
		this.firstname = firstname;
		this.lastname = lastname;
		this.middleName = middleName;
		this.userRole = userRole;
	}
}
