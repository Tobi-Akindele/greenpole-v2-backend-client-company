package com.ap.greenpole.clientCompanyModule.service.impl;

import com.ap.greenpole.clientCompanyModule.dtos.ShareHolderInMassRequestDto;
import com.ap.greenpole.clientCompanyModule.dtos.ShareHolderRequestDto;
import com.ap.greenpole.clientCompanyModule.dtos.ShareholderIntroductionDto;
import com.ap.greenpole.clientCompanyModule.entity.*;
import com.ap.greenpole.clientCompanyModule.exceptions.NotFoundException;
import com.ap.greenpole.clientCompanyModule.entity.ClientCompany;
import com.ap.greenpole.clientCompanyModule.entity.PagingHeaders;
import com.ap.greenpole.clientCompanyModule.entity.PagingResponse;
import com.ap.greenpole.clientCompanyModule.entity.Shareholder;
import com.ap.greenpole.clientCompanyModule.enums.GenericStatusEnum;
import com.ap.greenpole.clientCompanyModule.repositories.ClientCompanyRepository;
import com.ap.greenpole.clientCompanyModule.service.ClientCompanyService;
import com.ap.greenpole.clientCompanyModule.service.ShareHolderService;
import com.ap.greenpole.clientCompanyModule.utils.ExcelHelper;
import com.ap.greenpole.clientCompanyModule.service.ReportService;
import com.ap.greenpole.clientCompanyModule.utils.*;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nelu Akejelu on 18/08/2020.
 */


@Service
public class ClientCompanyServiceImpl implements ClientCompanyService {

    @Autowired
    ClientCompanyRepository clientCompanyRepository;

    @Autowired
    ReportService reportService;

    private final String resourceLocation;
    private final String downloadServer;
    private final String blobPath;
    private final String blobContainer;

    private static Logger log = LoggerFactory.getLogger(ClientCompanyServiceImpl.class);
    @Autowired
    ShareHolderService shareHolderService;

    public ClientCompanyServiceImpl(@Value("${resource.location}") String resourceLocation,
                                    @Value("${download.server}") String downloadServer,
                                    @Value("${blob.path}") String blobPath,
                                    @Value("${blob.container}") String blobContainer) {
        this.reportService = reportService;
        this.resourceLocation = resourceLocation;
        this.downloadServer = downloadServer;
        this.blobPath = blobPath;
        this.blobContainer = blobContainer;
    }

    @Override
    public ClientCompany save(ClientCompany clientCompany) {
        return clientCompanyRepository.save(clientCompany);
    }

    @Override
    public void delete(ClientCompany clientCompany) {
        clientCompanyRepository.delete(clientCompany);
    }

    @Override
    public List<ClientCompany> getAllClientCompanies() {
        return (List<ClientCompany>) clientCompanyRepository.findByStatusOrderByApprovedAtDesc(GenericStatusEnum.ACTIVE.name());
    }

    @Override
    public List<ClientCompany> getAllInactiveClientCompanies() {
        return (List<ClientCompany>) clientCompanyRepository.findByStatusOrderByApprovedAtDesc(GenericStatusEnum.INACTIVE.name());
    }

    @Override
    public ClientCompany getClientCompanyById(long id) {
        return clientCompanyRepository.getById(id);
    }

    @Override
    public PagingResponse get(Specification<ClientCompany> spec, HttpHeaders headers, Sort sort) {
        if (isRequestPaged(headers)) {
            return get(spec, buildPageRequest(headers, sort));
        } else {
            List<ClientCompany> entities = get(spec, sort);
            return new PagingResponse<>((long) entities.size(), 0L, 0L, 0L, 0L, entities);
        }
    }

    private boolean isRequestPaged(HttpHeaders headers) {
        return headers.containsKey(PagingHeaders.PAGE_NUMBER.getName())
                && headers.containsKey(PagingHeaders.PAGE_SIZE.getName());
    }

    private Pageable buildPageRequest(HttpHeaders headers, Sort sort) {
        int page = Integer.parseInt(headers.get(PagingHeaders.PAGE_NUMBER.getName()).get(0));
        int size = Integer.parseInt(headers.get(PagingHeaders.PAGE_SIZE.getName()).get(0));
        return PageRequest.of(page, size, sort);
    }

    private PagingResponse<ClientCompany> get(Specification<ClientCompany> spec, Pageable pageable) {
        Page<ClientCompany> page = clientCompanyRepository.findAll(spec, pageable);
        List<ClientCompany> content = page.getContent();
        return new PagingResponse<>(
                page.getTotalElements(),
                (long) page.getNumber(),
                (long) page.getNumberOfElements(),
                pageable.getOffset(),
                (long) page.getTotalPages(),
                content);
    }

    private List<ClientCompany> get(Specification<ClientCompany> spec, Sort sort) {
        return clientCompanyRepository.findAll(spec, sort);
    }

    public List<ShareholderIntroductionDto> parseFileToShareholderDto(MultipartFile file) throws IOException {
        List<ShareholderIntroductionDto> shareholderIntroductionDtos = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            if (index > 0) {
                ShareholderIntroductionDto shareholderIntroductionDto = new ShareholderIntroductionDto();
                XSSFRow row = worksheet.getRow(index);
                DataFormatter formatter = new DataFormatter();
                shareholderIntroductionDto.setBankAccount(formatter.formatCellValue(row.getCell(0)));
                shareholderIntroductionDto.setFirstName(row.getCell(1).getStringCellValue());
                shareholderIntroductionDto.setLastName(row.getCell(2).getStringCellValue());
                shareholderIntroductionDto.setMiddleName(row.getCell(3).getStringCellValue());
                shareholderIntroductionDto.setAddress(formatter.formatCellValue(row.getCell(4)));
                shareholderIntroductionDto.setShareUnit(new BigInteger(formatter.formatCellValue(row.getCell(5))));
                shareholderIntroductionDto.setStockBrokerName(row.getCell(6).getStringCellValue());
                shareholderIntroductionDto.setRegisterMandated(Boolean.valueOf(formatter.formatCellValue(row.getCell(7))));
                shareholderIntroductionDto.setTaxExemption(Boolean.valueOf(formatter.formatCellValue(row.getCell(8))));
                shareholderIntroductionDto.setPhone(formatter.formatCellValue(row.getCell(9)));
                shareholderIntroductionDto.setPostalCode(formatter.formatCellValue(row.getCell(10)));
                shareholderIntroductionDto.setShareholderType(formatter.formatCellValue(row.getCell(11)));
                shareholderIntroductionDto.setStockBrokerName(formatter.formatCellValue(row.getCell(12)));
                shareholderIntroductionDto.setNuban(formatter.formatCellValue(row.getCell(13)));
                shareholderIntroductionDto.setBankName(formatter.formatCellValue(row.getCell(14)));
                shareholderIntroductionDto.setBvn(formatter.formatCellValue(row.getCell(15)));
                shareholderIntroductionDto.setClearingHousingNumber(formatter.formatCellValue(row.getCell(16)));
                shareholderIntroductionDto.setEsopStatus(formatter.formatCellValue(row.getCell(17)));


                shareholderIntroductionDtos.add(shareholderIntroductionDto);
            }
        }
        return shareholderIntroductionDtos;
    }

    @Override
    public List<ShareHolderInMassRequestDto> parseFileToShareholderInMassDto(MultipartFile file)
            throws IOException {

        List<ShareHolderInMassRequestDto> inMassRequestDtos = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            if (index > 0) {
                ShareHolderInMassRequestDto requestDto = new ShareHolderInMassRequestDto();
                XSSFRow row = worksheet.getRow(index);
                DataFormatter formatter = new DataFormatter();

                requestDto.setNuban(formatter.formatCellValue(row.getCell(0)));
                requestDto.setFirstName(row.getCell(1).getStringCellValue());
                requestDto.setLastName(row.getCell(2).getStringCellValue());
                requestDto.setMiddleName(row.getCell(3).getStringCellValue());
                requestDto.setAddress(formatter.formatCellValue(row.getCell(4)));
                requestDto.setShareUnit(new BigInteger(formatter.formatCellValue(row.getCell(5))));
                requestDto.setKinName(row.getCell(6).getStringCellValue());
                requestDto.setKinAddress(row.getCell(7).getStringCellValue());
                requestDto.setKinEmail(row.getCell(8).getStringCellValue());
                requestDto.setPostalCode(formatter.formatCellValue(row.getCell(9)));
                requestDto.setClearingHouseNumber(formatter.formatCellValue(row.getCell(10)));
                requestDto.setEmail(row.getCell(11).getStringCellValue());
                requestDto.setCity(row.getCell(12).getStringCellValue());
                requestDto.setKinPhone(formatter.formatCellValue(row.getCell(13)));
                requestDto.setPhoneNumber(formatter.formatCellValue(row.getCell(14)));
                requestDto.setBankName(formatter.formatCellValue(row.getCell(15)));
                requestDto.setBvn(formatter.formatCellValue(row.getCell(16)));
                requestDto.setEsopStatus(formatter.formatCellValue(row.getCell(17)));
                requestDto.setShareholderType(formatter.formatCellValue(row.getCell(18)));
                requestDto.setStockBroker(formatter.formatCellValue(row.getCell(19)));

                inMassRequestDtos.add(requestDto);
            }
        }

        return inMassRequestDtos;
    }

    @Override
    public ByteArrayInputStream shareHolderToExcel(ClientCompany clientCompany) {
        List<Shareholder> shareholders = shareHolderService.findByClientCompany(clientCompany);
        return ExcelHelper.shareholdersToExcel(shareholders);
    }


    @Override
    public ClientCompanyReport exportClientCompaniesDetails(String format, List<ClientCompany> clientCompanies) {
        if(clientCompanies != null && clientCompanies.size() > 0) {
            return this.exportClientCompanyDetails(clientCompanies, format);
        }
        throw new NotFoundException("404", "No client company found");
    }

    private ClientCompanyReport exportClientCompanyDetails(List<ClientCompany> result, String format) {
        IFileGenerator<ClientCompany> generator = null;
        ClientCompanyReport clientCompanyReport = new ClientCompanyReport();
        String directories = resourceLocation + ConstantUtils.EXPORT_DIRECTORY;
        String filename = directories + ConstantUtils.CLIENT_COMPANY_EXPORT + "_"
                + Utils.getDateString(new Date()) + "_" + System.currentTimeMillis();
        filename = FilenameUtils.normalize(filename);
        filename = filename.replaceAll(" ", "_");

        try {
            if(ConstantUtils.SUPPORTED_EXPORT_FORMATS[0].equalsIgnoreCase(format)) {
                filename += ".csv";
                generator = new CsvFileGenerator<ClientCompany>(filename, new ClientCompany());
            }else if(ConstantUtils.SUPPORTED_EXPORT_FORMATS[1].equalsIgnoreCase(format)) {
                filename += ".xlsx";
                generator = new ExcelFileGenerator<ClientCompany>(filename, new ClientCompany());
            }else if(ConstantUtils.SUPPORTED_EXPORT_FORMATS[2].equalsIgnoreCase(format)) {
                filename += ".pdf";
                generator = new PdfFileGenerator<ClientCompany>(filename, new ClientCompany());
            }

            generator.createDirectory(directories);
            generator.open();
            generator.write(result);

            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(this.blobPath).buildClient();
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(this.blobContainer);
            BlobClient blobClient = containerClient.getBlobClient(filename);

            Path path = Paths.get(filename);

            //upload to blob from local path
            blobClient.uploadFromFile(path.resolve(filename).toAbsolutePath().toString());

            String url = blobClient.getBlobUrl();
//            String url = downloadServer + ConstantUtils.EXPORT_DIRECTORY + path.getFileName().toString();

            clientCompanyReport.setFilename(filename);
            clientCompanyReport.setFile_url(url);
            clientCompanyReport.setCreatedOn(java.time.LocalDateTime.now());
            reportService.create(clientCompanyReport);

            return clientCompanyReport;
        } catch (Exception ex) {
            log.error("Error occurred {}", ex);
            throw ex;
        }
    }

}
