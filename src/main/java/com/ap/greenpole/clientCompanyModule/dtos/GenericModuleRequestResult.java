package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GenericModuleRequestResult implements Serializable {

    @JsonProperty(value = "count")
    public Integer count;

    @JsonProperty(value = "data")
    public List<GenericModuleRequestResponse> requestResponseList;
}
