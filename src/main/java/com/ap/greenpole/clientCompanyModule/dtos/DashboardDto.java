package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class DashboardDto implements Serializable {

    @JsonProperty(value = "total_number_of_client_companies")
    private Long clientCompanyCount;

    @JsonProperty(value = "active_client_companies")
    private Integer activeClientCompanyCount;

    @JsonProperty(value = "inactive_client_companies")
    private Integer inActiveClientCompanyCount;

    @JsonProperty(value = "active_share_holders_count")
    private int activeShareHoldersCount;

    @JsonProperty(value = "inactive_share_holders_count")
    private int inactiveShareHoldersCount;
}
