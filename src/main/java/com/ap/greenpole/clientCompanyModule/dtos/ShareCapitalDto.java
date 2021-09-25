package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@Builder(toBuilder = true)
public class ShareCapitalDto implements Serializable {

    @JsonProperty(value = "authorized_share_capital")
    public BigInteger authorizedShareCapital;

    @JsonProperty(value = "paid_up_share_capital")
    public BigInteger paidUpShareCapital;

    @JsonProperty(value = "client_company_name")
    public String clientCompanyName;

    @JsonProperty(value = "client_company_id")
    public long clientCompanyId;

    @JsonProperty(value = "client_company_code")
    public String clientCompanyRegisterCode;

    @JsonProperty(value = "variance")
    public BigInteger variance;

    @JsonProperty(value = "holders_count")
    public Integer holdersCount;

}
