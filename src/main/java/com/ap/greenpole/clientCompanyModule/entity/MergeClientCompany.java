package com.ap.greenpole.clientCompanyModule.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;;
import java.util.List;


/**
 * Created by Lewis.Aguh on 27/08/2020.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MergeClientCompany {
    private Long primaryCompanyId;
    private List<Long> secondaryCompaniesIds;
}
