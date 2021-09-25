package com.ap.greenpole.clientCompanyModule.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * Created by Lewis.Aguh on 09/09/2020.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "votes")
@Builder
public class Votes implements Serializable {

    private static final long serialVersionUID = 5146914376303226067L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="voting_process_id")
    @JsonManagedReference
    private VotingProcess votingProcess;

    private String verdict;

    @Column(name = "shareholder_id")
    private Long shareholder_id;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "voted_at")
    private LocalDateTime voted_at;
}
