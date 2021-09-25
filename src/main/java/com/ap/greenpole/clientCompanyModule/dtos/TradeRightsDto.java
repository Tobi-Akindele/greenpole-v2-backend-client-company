package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

@Data
public class TradeRightsDto implements Serializable {

    @NotNull
    @JsonProperty(value = "buyer_id")
    public long buyerId;

    @NotNull
    @JsonProperty(value = "seller_id")
    public long sellerId;

    @NotNull
    @JsonProperty(value = "rights")
    public BigInteger rights;
}
