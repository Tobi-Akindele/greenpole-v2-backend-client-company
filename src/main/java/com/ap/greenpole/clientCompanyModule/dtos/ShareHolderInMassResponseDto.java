package com.ap.greenpole.clientCompanyModule.dtos;

import com.ap.greenpole.clientCompanyModule.enums.GenericStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Data
public class ShareHolderInMassResponseDto implements Serializable {

    @JsonProperty(value = "shareholder_id")
    public long shareHolderId;

    @JsonProperty(value = "first_name")
    public String firstName;

    @JsonProperty(value = "middle name")
    public String middleName;

    @JsonProperty(value = "last_name")
    public String lastName;

    @JsonProperty(value = "email")
    public String email;

    @JsonProperty(value = "nuban")
    public String nuban;

    @JsonProperty(value = "address")
    public String address;

    @JsonProperty(value = "share_units")
    public BigInteger shareUnit;

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

    @JsonProperty(value = "clearing_house_number")
    public String clearingHouseNumber;

    @JsonProperty(value = "created_on")
    public Date createdDate;

    @JsonProperty(value = "status")
    public GenericStatusEnum status;
}
