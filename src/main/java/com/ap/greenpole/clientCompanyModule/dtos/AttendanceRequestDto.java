package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;


@Data
public class AttendanceRequestDto implements Serializable {

    @NotNull
    @JsonProperty(value = "shareholder_ids")
    public List<Long> attendeesIds;

}
