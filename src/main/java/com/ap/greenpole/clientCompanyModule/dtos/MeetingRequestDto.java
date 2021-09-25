package com.ap.greenpole.clientCompanyModule.dtos;

import com.ap.greenpole.clientCompanyModule.enums.MeetingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by Nelu Akejelu on 18/08/2020.
 */
@Data
public class MeetingRequestDto implements Serializable {

    @JsonProperty(value = "title")
    @NotBlank
    public String title;

    @JsonProperty(value = "purpose")
    @NotBlank
    public String purpose;

    @JsonProperty(value = "meeting_type")
    @NotNull
    public MeetingType meetingType;

    @JsonProperty(value = "other_notes")
    @NotBlank
    public String otherNotes;

    @JsonProperty(value = "start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @NotNull
    public LocalDateTime startDate;

    @JsonProperty(value = "end_date")
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    public LocalDateTime endDate;
}
