package com.ap.greenpole.clientCompanyModule.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * Created by Lewis.Aguh on 09/09/2020.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "voting_process")
@Builder
public class VotingProcess implements Serializable {

    private static final long serialVersionUID = 5146914376303226067L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="meeting_id")
    @JsonManagedReference
    private Meeting meeting;

    private String title;

    @Lob
    @Column(name = "notes", length = 512)
    private String notes;

    @Column(name = "client_company_id")
    private Long clientCompanyId;

    private String status;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_on")
    private LocalDateTime created_on;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "ended_at")
    private LocalDateTime ended_at;
}
