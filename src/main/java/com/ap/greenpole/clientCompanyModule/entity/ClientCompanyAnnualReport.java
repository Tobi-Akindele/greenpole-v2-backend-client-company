package com.ap.greenpole.clientCompanyModule.entity;


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
@Entity(name = "client_company_annual_report")
@Builder
public class ClientCompanyAnnualReport implements Serializable {

    private static final long serialVersionUID = 5146914376303226067L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    private String title;

    @Column(name = "report_year")
    private String report_year;

//    @Column(name = "file_url")
//    private String file_url;

    @Column(name = "client_company_id")
    private Long client_company_id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="client_company_report_id")
    @JsonManagedReference
    private ClientCompanyReport clientCompanyReport;
}
