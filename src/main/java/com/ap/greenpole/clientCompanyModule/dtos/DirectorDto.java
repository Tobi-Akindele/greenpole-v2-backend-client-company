package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Nelu Akejelu on 18/08/2020.
 */

@Data
public class DirectorDto implements Serializable {


    @JsonProperty(value = "id")
    public long id;

    @JsonProperty(value = "first_name")
    public String firstName;

    @JsonProperty(value = "middle_name")
    public String middleName;

    @JsonProperty(value = "last_name")
    public String lastName;

    @JsonProperty(value = "email")
    public String email;

    @JsonProperty(value = "phone_number")
    public String phoneNumber;

}
