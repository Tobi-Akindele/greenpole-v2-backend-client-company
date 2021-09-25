package com.ap.greenpole.clientCompanyModule.dtos;

import com.ap.greenpole.clientCompanyModule.enums.GenericStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by Nelu Akejelu on 18/08/2020.
 */

@Data
public class ClientCompanyResponseDto implements Serializable {

    @JsonProperty(value = "id")
    public Long id;

    @JsonProperty(value = "status")
    public GenericStatusEnum status;

    @JsonProperty(value = "register_name")
    public String registerName;

    @JsonProperty(value = "register_code")
    public String registerCode;

    @JsonProperty(value = "symbol")
    public String symbol;

    @JsonProperty(value = "chairman_name")
    public String chairmanName;

    @JsonProperty(value = "ceo_name")
    public String ceoName;

    @JsonProperty(value = "registration_code")
    public String registrationCode;

    @JsonProperty(value = "address")
    public String address;

    @JsonProperty(value = "email_address")
    public String emailAddress;

    @JsonProperty(value = "directors")
    public List<DirectorDto> directors;

    @JsonProperty(value = "share_holders")
    public List<ShareHolderResponseDto> shareHolders;

    @JsonProperty(value = "rc_number")
    public String rcNumber;

    @JsonProperty(value = "country")
    public String country;

    @JsonProperty(value = "state")
    public String state;

    @JsonProperty(value = "lga")
    public String lga;

    @JsonProperty(value = "postal_code")
    public String postalCode;

    @JsonProperty(value = "alternate_phone_number")
    public String alternatePhoneNumber;

    @JsonProperty(value = "phone_number")
    public String phoneNumber;

    @JsonProperty(value = "depository")
    public String depository;

    @JsonProperty(value = "exchange")
    public String exchange; //NSE, NASD, FMDQ

    @JsonProperty(value = "date_of_incorporation")
    public Date dateOfIncorporation;

    @JsonProperty(value = "secretary")
    public String secretary;

    @JsonProperty(value = "nse_sector")
    public String nseSector;

    @JsonProperty(value = "authorized_share_capital")
    public BigInteger authorizedShareCapital;

    @JsonProperty(value = "paid_up_share_capital")
    public BigInteger paidUpShareCapital;
}
