package com.ap.greenpole.clientCompanyModule.service;

import com.ap.greenpole.clientCompanyModule.dtos.ShareHolderInMassRequestDto;
import com.ap.greenpole.clientCompanyModule.dtos.ShareHolderRequestDto;
import com.ap.greenpole.clientCompanyModule.dtos.ShareholderIntroductionDto;
import com.ap.greenpole.clientCompanyModule.entity.ClientCompany;
import com.ap.greenpole.clientCompanyModule.entity.ClientCompanyReport;
import com.ap.greenpole.clientCompanyModule.entity.PagingResponse;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Nelu Akejelu on 18/08/2020.
 */

public interface ClientCompanyService {

    ClientCompany save(ClientCompany clientCompany);
    void delete(ClientCompany clientCompany);
    List<ClientCompany> getAllClientCompanies();
    List<ClientCompany> getAllInactiveClientCompanies();
    ClientCompany getClientCompanyById(long id);
    PagingResponse get(Specification<ClientCompany> spec, HttpHeaders headers, Sort sort);
    List<ShareholderIntroductionDto> parseFileToShareholderDto(MultipartFile file) throws IOException;
    List<ShareHolderInMassRequestDto> parseFileToShareholderInMassDto(MultipartFile file) throws IOException;
     ByteArrayInputStream shareHolderToExcel(ClientCompany clientCompany);
     ClientCompanyReport exportClientCompaniesDetails(String format, List<ClientCompany> clientCompanies);
}
