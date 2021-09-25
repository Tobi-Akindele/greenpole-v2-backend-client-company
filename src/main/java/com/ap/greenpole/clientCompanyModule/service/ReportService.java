package com.ap.greenpole.clientCompanyModule.service;

import com.ap.greenpole.clientCompanyModule.entity.ClientCompanyReport;
import com.ap.greenpole.clientCompanyModule.entity.Result;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Lewis.Aguh on 19/08/2020
 */


public interface ReportService {

    ClientCompanyReport create(ClientCompanyReport clientCompanyReport);
    Result<ClientCompanyReport> getAllReports(int pageNumber, int pageSize, Pageable pageable);
    Result<ClientCompanyReport> searchReports(String filename, String fromDate, String toDate, int pageNumber, int pageSize, Pageable pageable);
    void storeFile(MultipartFile file, String filename, String targetLocation);
}
