package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class DirectorRequestDto implements Serializable {

    @JsonProperty(value = "first_name")
    public String firstName;

    @JsonProperty(value = "middle_name")
    public String middleName;

    @JsonProperty(value = "last_name")
    public String lastName;

    @JsonProperty(value = "email")
    public String email;

    @JsonProperty("phone_number")
    public String phoneNumber;
}
