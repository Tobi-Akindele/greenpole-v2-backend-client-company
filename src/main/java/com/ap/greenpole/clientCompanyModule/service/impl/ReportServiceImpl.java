package com.ap.greenpole.clientCompanyModule.service.impl;

import com.ap.greenpole.clientCompanyModule.entity.*;
import com.ap.greenpole.clientCompanyModule.exceptions.BadRequestException;
import com.ap.greenpole.clientCompanyModule.repositories.ReportRepository;
import com.ap.greenpole.clientCompanyModule.service.ReportService;
import com.ap.greenpole.clientCompanyModule.utils.IFileGenerator;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

/**
 * Created by Lewis.Aguh on 19/08/2020
 */

@Service
public class ReportServiceImpl implements ReportService {

	@Autowired
	ReportRepository reportRepository;

	private static Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

	private final String blobPath;
	private final String blobContainer;

	public ReportServiceImpl(@Value("${blob.path}") String blobPath, @Value("${blob.container}") String blobContainer) {
		this.blobPath = blobPath;
		this.blobContainer = blobContainer;
	}

	@Override
	public ClientCompanyReport create(ClientCompanyReport clientCompanyReport) {
		return reportRepository.save(clientCompanyReport);
	}

	@Override
	public Result<ClientCompanyReport> getAllReports(int pageNumber, int pageSize, Pageable pageable) {
		Page<ClientCompanyReport> allRecords = reportRepository.findAll(pageable);
		long noOfRecords = allRecords.getTotalElements();
		Result<ClientCompanyReport> result =
				new Result<>(0, allRecords.getContent(), noOfRecords, pageNumber, pageSize);

		return result;
	}

	@Override
	public Result<ClientCompanyReport> searchReports(String filename, String fromDate, String toDate, int pageNumber, int pageSize, Pageable pageable) {
		Page<ClientCompanyReport> allRecords = reportRepository.searchReports(filename, fromDate, toDate, pageable);
		long noOfRecords = allRecords.getTotalElements();
		Result<ClientCompanyReport> result =
				new Result<>(0, allRecords.getContent(), noOfRecords, pageNumber, pageSize);

		return result;
	}

	public void createDirectory(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			boolean flag = file.mkdirs();
			if (flag) {
				log.debug("Directory is created!");
			} else {
				log.error("Directory creation failed");
			}
		} else {
			log.debug("Directory already exists.");
		}
	}

	@Override
	public void storeFile(MultipartFile file, String filename, String targetLocation) {
		// copy file to the target location (Replacing existing file with the same name)
		try {
			// create dir
			this.createDirectory(filename);

			Path location = Paths.get(filename);

			BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(this.blobPath).buildClient();
			BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(this.blobContainer);
			BlobClient blobClient = containerClient.getBlobClient(filename);

			Files.copy(file.getInputStream(), location, StandardCopyOption.REPLACE_EXISTING);

			//upload to blob from local path
			blobClient.uploadFromFile(location.resolve(filename).toAbsolutePath().toString());
		} catch (IOException ex) {
			throw new BadRequestException("400", "Could not store file " + filename + " Please try again!");
		}

	}
}
