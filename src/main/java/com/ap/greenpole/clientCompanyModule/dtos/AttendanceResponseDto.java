package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class AttendanceResponseDto implements Serializable {

    @JsonProperty(value = "meeting_id")
    long meetingId;

    @JsonProperty(value = "attendees")
    List<ShareHolderResponseDto> attendees;

}
