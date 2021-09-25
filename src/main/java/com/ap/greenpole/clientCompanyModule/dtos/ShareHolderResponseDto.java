package com.ap.greenpole.clientCompanyModule.dtos;

import com.ap.greenpole.clientCompanyModule.enums.GenericStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.UUID;

@Data
public class ShareHolderResponseDto implements Serializable {

    @JsonProperty(value = "share_holder_id")
    public long shareHolderId;

    @JsonProperty(value = "first_name")
    public String firstName;

    @JsonProperty(value = "middle name")
    public String middleName;

    @JsonProperty(value = "last_name")
    public String lastName;

    @JsonProperty(value = "email")
    public String email;

    @JsonProperty(value = "status")
    public GenericStatusEnum status;

    @JsonProperty(value = "account")
    public String account;

    @JsonProperty(value = "address")
    public String address;

    @JsonProperty(value = "holding")
    public BigInteger holding;
}
