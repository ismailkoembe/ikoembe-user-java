package com.ikoembe.study.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
