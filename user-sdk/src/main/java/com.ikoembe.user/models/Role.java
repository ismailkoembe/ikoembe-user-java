package com.ikoembe.user.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Role {
//  @Id
//  private String id;

  private ERole name;

}
