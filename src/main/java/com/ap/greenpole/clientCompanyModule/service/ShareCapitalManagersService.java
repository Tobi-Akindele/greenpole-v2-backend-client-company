package com.ap.greenpole.clientCompanyModule.service;

import com.ap.greenpole.clientCompanyModule.entity.Result;
import com.ap.greenpole.clientCompanyModule.entity.ShareCapitalManagers;
import org.springframework.data.domain.Pageable;

/**
 * Created by Lewis.Aguh on 04/10/2020
 */


public interface ShareCapitalManagersService {

    ShareCapitalManagers create(ShareCapitalManagers shareCapitalManagers);
    Result<ShareCapitalManagers> getAllShareCapitalManagers(int pageNumber, int pageSize, Pageable pageable, Long clientCompanyId);
    ShareCapitalManagers getManagerByNameAndPosition(String name, String position, Long clientCompanyId);
}
