package com.ap.greenpole.clientCompanyModule.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Builder
public class ShareCapitalChecklist{

    private Long clientCompanyId;

    private String registerName;

    private String registerCode;

    private Long authorisedShareCapital;

    private Long paidUpShareCapital;
}
