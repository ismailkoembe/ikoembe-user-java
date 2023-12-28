package com.ikoembe.study.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Ismail Koembe
 */
@Data
public class Major {
    @JsonProperty("name")
    private String name;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("isPrimary")
    private boolean isPrimary;
}
