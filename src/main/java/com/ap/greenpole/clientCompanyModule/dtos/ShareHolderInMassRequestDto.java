package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;


@Data
public class ShareHolderInMassRequestDto {

    @NotEmpty
    @JsonProperty(value = "first_name")
    public String firstName;

    @NotEmpty
    @JsonProperty(value = "middle_name")
    public String middleName;

    @NotEmpty
    @JsonProperty(value = "last_name")
    public String lastName;

    @NotEmpty
    @JsonProperty(value = "email")
    public String email;

    @NotEmpty
    @JsonProperty(value = "nuban")
    public String nuban;

    @NotEmpty
    @JsonProperty(value = "address")
    public String address;

    @NotNull
    @JsonProperty(value = "share_units")
    public BigInteger shareUnit;

    @NotEmpty
    @JsonProperty(value = "next_of_kin_name")
    public String kinName;

    @NotEmpty
    @JsonProperty(value = "next_of_kin_number")
    public String kinPhone;

    @NotEmpty
    @JsonProperty(value = "next_of_kin_address")
    public String kinAddress;

    @NotEmpty
    @JsonProperty(value = "next_of_kin_email")
    public String kinEmail;

    @NotEmpty
    @JsonProperty(value = "postal_code")
    public String postalCode;

    @JsonProperty(value = "city")
    public String city;

    @JsonProperty(value = "phone_number")
    public String phoneNumber;

    @NotEmpty
    @JsonProperty(value = "clearing_house_number")
    public String clearingHouseNumber;

    @NotEmpty
    @JsonProperty(value = "bank_name")
    public String bankName;


    @NotEmpty
    @JsonProperty(value = "bvn")
    public String bvn;

    @NotEmpty
    @JsonProperty(value = "esop_status")
    public String esopStatus;

    @NotEmpty
    @JsonProperty(value = "shareholder_type")
    public String shareholderType;

    @NotEmpty
    @JsonProperty(value = "stockBroker")
    public String stockBroker;
}
