package com.ap.greenpole.clientCompanyModule.entity;

import com.ap.greenpole.clientCompanyModule.enums.MeetingType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
/**
 * Created by Nelu Akejelu on 18/08/2020.
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "meeting")
@Builder
public class Meeting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;

    @Column(name = "title")
    private String title;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "meeting_type")
    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;

    @Column(name = "other_notes")
    private String otherNotes;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    private long clientCompanyId;

    @ElementCollection
    private List<Long> attendees;
}
