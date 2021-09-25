package com.ap.greenpole.clientCompanyModule.service;

import com.ap.greenpole.clientCompanyModule.dtos.ShareCapitalDto;
import com.ap.greenpole.clientCompanyModule.dtos.ShareHolderInMassRequestDto;
import com.ap.greenpole.clientCompanyModule.entity.ClientCompany;
import com.ap.greenpole.clientCompanyModule.entity.Error;
import com.ap.greenpole.clientCompanyModule.entity.ShareCapitalChecklist;
import com.ap.greenpole.clientCompanyModule.entity.Votes;
import com.ap.greenpole.clientCompanyModule.entity.VotingProcess;
import com.ap.greenpole.clientCompanyModule.exceptions.BadRequestException;
import com.ap.greenpole.clientCompanyModule.exceptions.NotFoundException;
import com.ap.greenpole.clientCompanyModule.utils.ConstantUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Lewis.Aguh on 16/09/2020
 */

@Service
public class UploadShareCapitalChecklistService {

    @Autowired
    ClientCompanyService clientCompanyService;

    public ResponseEntity<?> processExcel(MultipartFile file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        // get header names
        List<String> headernames = this.getHeaderName(worksheet);
        List<String> validHeaderNames = Arrays.asList(ConstantUtils.SHARE_CAPITAL_HEADER_NAMES);

        List<ShareCapitalChecklist> shareCapitalChecklists = new ArrayList<>();

        Boolean areHeadersCorrect = new HashSet<>(headernames).equals(new HashSet<>(validHeaderNames));
        if(!areHeadersCorrect)
            return new ResponseEntity<>("Header column names are not correct", HttpStatus.BAD_REQUEST);

        // get excel content to a list
        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            if (index > 0) {
                ShareCapitalChecklist requestDto = new ShareCapitalChecklist();
                XSSFRow row = worksheet.getRow(index);
                DataFormatter formatter = new DataFormatter();

                requestDto.setClientCompanyId(Long.valueOf(row.getCell(0).getStringCellValue()));
                requestDto.setRegisterName(row.getCell(1).getStringCellValue());
                requestDto.setRegisterCode(row.getCell(2).getStringCellValue());
                requestDto.setAuthorisedShareCapital(Long.valueOf(row.getCell(3).getStringCellValue()));
                requestDto.setPaidUpShareCapital(Long.valueOf(row.getCell(4).getStringCellValue()));

                shareCapitalChecklists.add(requestDto);
            }
        }

        // validate list
        List<Error> errors = new ArrayList<>();

        for(int i = 0; i < shareCapitalChecklists.size(); i++) {

            if(shareCapitalChecklists.get(i).getClientCompanyId() == null) {
                Error error = new Error(null, "Client company is required at index: " + i);
                errors.add(error);
            } else {
                ClientCompany clientCompany = clientCompanyService.getClientCompanyById(shareCapitalChecklists.get(i).getClientCompanyId());
                if (clientCompany == null) {
                    Error error = new Error(null, "Client company ID : " + shareCapitalChecklists.get(i).getClientCompanyId() + " at index " + i + " does not exist or has been deactivated");
                    errors.add(error);
                }
            }

            if(shareCapitalChecklists.get(i).getAuthorisedShareCapital() == null || shareCapitalChecklists.get(i).getAuthorisedShareCapital() <= 0) {
                Error error = new Error(null, "Authorised share capital at index: " + i + " must be greater than zero");
                errors.add(error);
            }

            if(shareCapitalChecklists.get(i).getPaidUpShareCapital() == null || shareCapitalChecklists.get(i).getPaidUpShareCapital() <= 0) {
                Error error = new Error(null, "Paid up share capital at index: " + i + " must be greater than zero");
                errors.add(error);
            }

        }

        if (!errors.isEmpty())
            return new ResponseEntity<>(errors.toString(), HttpStatus.BAD_REQUEST);

        List<ClientCompany> clientCompanies = new ArrayList<>();

        // get client companies and persist in db
        for (ShareCapitalChecklist shareCapitalChecklist: shareCapitalChecklists) {
            ClientCompany clientCompany = clientCompanyService.getClientCompanyById(shareCapitalChecklist.getClientCompanyId());
            clientCompany.setPaidUpShareCapital(BigInteger.valueOf(shareCapitalChecklist.getPaidUpShareCapital()));
            clientCompany.setAuthorizedShareCapital(BigInteger.valueOf(shareCapitalChecklist.getAuthorisedShareCapital()));

            clientCompany = clientCompanyService.save(clientCompany);
            clientCompanies.add(clientCompany);
        }

        return new ResponseEntity<>(clientCompanies, HttpStatus.OK);
    }

    List<String> getHeaderName(Sheet sheet) {
        List<String> headers = new ArrayList<>();
        Row row;
        int headerId = 0;
        if(sheet.getRow(headerId) != null && sheet.getRow(headerId).cellIterator() != null) {
            row = sheet.getRow(headerId);
            for(int h = 0; h <= row.getLastCellNum(); h++) {
                if(row.getCell((short) h) != null && row.getCell((short) h).toString().length() != 0) {
                    headers.add(row.getCell((short) h).toString());
                }
            }
        }

        return headers;
    }


}
