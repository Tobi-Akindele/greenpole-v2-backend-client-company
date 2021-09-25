package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigInteger;

@Data
public class ShareholderIntroductionDto implements Serializable {


    @JsonProperty(value = "client_company_id")
    public long clientCompanyId;

    @JsonProperty(value = "first_name")
    @NotEmpty
    public String firstName;

    @JsonProperty(value = "middle_name")
    @NotEmpty
    public String middleName;

    @JsonProperty(value = "last_name")
    @NotEmpty
    public String lastName;

    @JsonProperty(value = "clearing_house_number")
    @NotEmpty
    public String clearingHousingNumber;

    @JsonProperty(value = "bank_name")
    public String bankName;

    @JsonProperty(value = "email")
    @NotEmpty
    public String email;

    @JsonProperty(value = "phoneNumber")
    @NotEmpty
    public String phone;

    @JsonProperty(value = "bvn")
    @NotEmpty
    public String bvn;

    @JsonProperty(value = "nuban")
    @NotEmpty
    public String nuban;

    @JsonProperty(value = "esopStatus")
    @NotEmpty
    public String esopStatus;

    @JsonProperty(value = "bank_account")
    @NotEmpty
    public String bankAccount;

    @JsonProperty(value = "address")
    public String address;

    @JsonProperty(value = "share_unit")
    public BigInteger shareUnit;

    @JsonProperty(value = "stock_broker_name")
    public String stockBrokerName;

    @JsonProperty(value = "register_mandated")
    public Boolean registerMandated;

    @JsonProperty(value = "tax_exemption")
    public Boolean taxExemption;

    @JsonProperty(value = "postal_code")
    public String postalCode;

    @JsonProperty(value = "share_holder_type")
    public String shareholderType;
}
