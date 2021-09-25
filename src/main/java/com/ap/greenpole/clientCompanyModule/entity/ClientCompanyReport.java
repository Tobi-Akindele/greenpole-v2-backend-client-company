package com.ap.greenpole.clientCompanyModule.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


/**
 * Created by Lewis.Aguh on 19/08/2020.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "client_company_report")
@Builder
public class ClientCompanyReport implements Serializable {

    private static final long serialVersionUID = 5146914376303226067L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    private String filename;

    @Column(name = "file_url")
    private String file_url;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable=false, name = "created_on")
    protected LocalDateTime createdOn;

    @Column(name = "is_deleted")
    private boolean isDeleted;
}
