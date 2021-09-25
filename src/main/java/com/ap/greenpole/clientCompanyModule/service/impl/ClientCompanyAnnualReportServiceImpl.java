package com.ap.greenpole.clientCompanyModule.service.impl;

import com.ap.greenpole.clientCompanyModule.entity.ClientCompanyAnnualReport;
import com.ap.greenpole.clientCompanyModule.entity.Votes;
import com.ap.greenpole.clientCompanyModule.entity.VotingProcess;
import com.ap.greenpole.clientCompanyModule.repositories.ClientCompanyAnnualReportRepository;
import com.ap.greenpole.clientCompanyModule.repositories.VotesRepository;
import com.ap.greenpole.clientCompanyModule.service.ClientCompanyAnnualReportService;
import com.ap.greenpole.clientCompanyModule.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Lewis Aguh on 18/09/2020.
 */

@Service
public class ClientCompanyAnnualReportServiceImpl implements ClientCompanyAnnualReportService {

    @Autowired
    ClientCompanyAnnualReportRepository clientCompanyAnnualReportRepository;

    @Override
    public ClientCompanyAnnualReport create(ClientCompanyAnnualReport clientCompanyAnnualReport) {
        return clientCompanyAnnualReportRepository.save(clientCompanyAnnualReport);
    }
}
