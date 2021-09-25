package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by Nelu Akejelu on 18/08/2020.
 */

@Data
public class ClientCompanyRequestDto implements Serializable {

    @NotBlank
    @JsonProperty(value = "register_name")
    public String registerName;

    @NotBlank
    @JsonProperty(value = "register_code")
    public String registerCode;

    @NotBlank
    @JsonProperty(value = "symbol")
    public String symbol;

    @NotBlank
    @JsonProperty(value = "chairman_name")
    public String chairmanName;

    @NotBlank
    @JsonProperty(value = "ceo_name")
    public String ceoName;

    @NotBlank
    @JsonProperty(value = "registration_code")
    public String registrationCode;

    @NotBlank
    @JsonProperty(value = "address")
    public String address;

    @NotBlank
    @JsonProperty(value = "email_address")
    public String emailAddress;

    @NotNull
    @JsonProperty(value = "directors")
    public List<DirectorRequestDto> directors;

    @NotBlank
    @JsonProperty(value = "phone_number")
    public String phoneNumber;

    @NotBlank
    @JsonProperty(value = "depository")
    public String depository;

    @NotBlank
    @JsonProperty(value = "exchange")
    public String exchange; //NSE, NASD, FMDQ

    @JsonProperty(value = "date_of_incorporation")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    public Date dateOfIncorporation;

    @NotBlank
    @JsonProperty(value = "secretary")
    public String secretary;

    @NotBlank
    @JsonProperty(value = "nse_sector")
    public String nseSector;

    @NotBlank
    @JsonProperty(value = "rc_number")
    public String rcNumber;

    @NotBlank
    @JsonProperty(value = "country")
    public String country;

    @NotBlank
    @JsonProperty(value = "state")
    public String state;

    @NotBlank
    @JsonProperty(value = "lga")
    public String lga;

    @NotBlank
    @JsonProperty(value = "postal_code")
    public String postalCode;

    @NotBlank
    @JsonProperty(value = "alternate_phone_number")
    public String alternatePhoneNumber;
}
