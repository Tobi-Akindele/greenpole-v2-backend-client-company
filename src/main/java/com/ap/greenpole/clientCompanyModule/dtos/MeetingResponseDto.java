package com.ap.greenpole.clientCompanyModule.dtos;

import com.ap.greenpole.clientCompanyModule.enums.MeetingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class MeetingResponseDto {

    @JsonProperty(value = "meeting_id")
    long id;

    @JsonProperty(value = "title")
    public String title;

    @JsonProperty(value = "purpose")
    public String purpose;

    @JsonProperty(value = "meeting_type")
    public MeetingType meetingType;

    @JsonProperty(value = "other_notes")
    public String otherNotes;

    @JsonProperty(value = "start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    public LocalDateTime startDate;

    @JsonProperty(value = "end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    public LocalDateTime endDate;

    @JsonProperty(value = "date_created")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    public LocalDateTime dateCreated;
}
