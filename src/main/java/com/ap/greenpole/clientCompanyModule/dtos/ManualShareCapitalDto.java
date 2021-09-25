package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

@Data
@Builder(toBuilder = true)
public class ManualShareCapitalDto implements Serializable {

    @NotNull
    @JsonProperty(value = "authorized_share_capital")
    public BigInteger authorizedShareCapital;

    @NotNull
    @JsonProperty(value = "paid_up_share_capital")
    public BigInteger paidUpShareCapital;
}
