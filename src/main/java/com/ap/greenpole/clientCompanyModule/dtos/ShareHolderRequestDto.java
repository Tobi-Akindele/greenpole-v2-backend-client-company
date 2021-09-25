package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.UUID;

/**
 * Created by Nelu Akejelu on 18/08/2020.
 */


@Data
public class ShareHolderRequestDto implements Serializable {


    @JsonProperty(value = "client_company_id")
    public long clientCompanyId;

    @JsonProperty(value = "first_name")
    public String firstName;

    @JsonProperty(value = "middle_name")
    public String middleName;

    @JsonProperty(value = "last_name")
    public String lastName;

    @JsonProperty(value = "clearing_house_number")
    public String clearingHousingNumber;

    @JsonProperty(value = "bank_name")
    public String bankName;

    @JsonProperty(value = "email")
    public String email;

    @JsonProperty(value = "phoneNumber")
    public String phone;

    @JsonProperty(value = "bvn")
    public String bvn;

    @JsonProperty(value = "bank_account")
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

}
