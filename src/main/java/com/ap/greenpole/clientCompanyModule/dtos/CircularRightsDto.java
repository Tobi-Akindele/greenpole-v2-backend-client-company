package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class CircularRightsDto implements Serializable {

    @JsonProperty(value = "first_name")
    public String firstName;

    @JsonProperty(value = "middle name")
    public String middleName;

    @JsonProperty(value = "last_name")
    public String lastName;

    @JsonProperty(value = "email")
    public String email;

    @JsonProperty(value = "client_company")
    public String clientCompany;

    @JsonProperty(value = "gender")
    public String gender;

    @JsonProperty(value = "occupation")
    public String occupation;

    @JsonProperty(value = "marital_status")
    public String maritalStatus;

    @JsonProperty(value = "state_of_origin")
    public String stateOfOrigin;

    @JsonProperty(value = "lga_of_origin")
    public String lgaOfOrigin;

    @JsonProperty(value = "bank_account")
    public String account;

    @JsonProperty(value = "address")
    public String address;

    @JsonProperty(value = "share_units")
    public BigInteger shareUnits;

    @JsonProperty(value = "next_of_kin_name")
    public String kinName;

    @JsonProperty(value = "next_of_kin_number")
    public String kinPhone;

    @JsonProperty(value = "next_of_kin_address")
    public String kinAddress;

    @JsonProperty(value = "next_of_kin_email")
    public String kinEmail;

    @JsonProperty(value = "postal_code")
    public String postalCode;

    @JsonProperty(value = "city")
    public String city;

    @JsonProperty(value = "phone_number")
    public String phoneNumber;

    @JsonProperty(value = "nuban")
    public String nuban;
}
