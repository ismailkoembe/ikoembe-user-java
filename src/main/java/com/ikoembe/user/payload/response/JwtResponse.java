package com.ikoembe.user.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String id;

	public JwtResponse(String token, String id) {
		this.token = token;
		this.id = id;
	}
}
