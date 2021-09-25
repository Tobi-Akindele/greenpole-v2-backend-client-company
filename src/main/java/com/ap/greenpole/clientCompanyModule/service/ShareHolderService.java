package com.ap.greenpole.clientCompanyModule.service;

import com.ap.greenpole.clientCompanyModule.entity.ClientCompany;
import com.ap.greenpole.clientCompanyModule.entity.Shareholder;

import java.util.List;

public interface ShareHolderService {

    List<Shareholder> findByClientCompany(ClientCompany clientCompany);
    Shareholder findById(long id);
    List<Shareholder> getActiveShareHolders();
    List<Shareholder> getInActiveShareHolders();
    List<Shareholder> findByBvn(String bvn);

}
