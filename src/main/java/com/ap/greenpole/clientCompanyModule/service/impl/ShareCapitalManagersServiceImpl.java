package com.ap.greenpole.clientCompanyModule.service.impl;

import com.ap.greenpole.clientCompanyModule.entity.ClientCompanyReport;
import com.ap.greenpole.clientCompanyModule.entity.Result;
import com.ap.greenpole.clientCompanyModule.entity.ShareCapitalManagers;
import com.ap.greenpole.clientCompanyModule.exceptions.BadRequestException;
import com.ap.greenpole.clientCompanyModule.repositories.ReportRepository;
import com.ap.greenpole.clientCompanyModule.repositories.ShareCapitalManagersRepository;
import com.ap.greenpole.clientCompanyModule.service.ReportService;
import com.ap.greenpole.clientCompanyModule.service.ShareCapitalManagersService;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Created by Lewis.Aguh on 19/08/2020
 */

@Service
public class ShareCapitalManagersServiceImpl implements ShareCapitalManagersService {

	@Autowired
	ShareCapitalManagersRepository shareCapitalManagersRepository;

	private static Logger log = LoggerFactory.getLogger(ShareCapitalManagersServiceImpl.class);

	@Override
	public ShareCapitalManagers create(ShareCapitalManagers shareCapitalManagers) {
		return shareCapitalManagersRepository.save(shareCapitalManagers);
	}

	@Override
	public Result<ShareCapitalManagers> getAllShareCapitalManagers(int pageNumber, int pageSize, Pageable pageable, Long clientCompanyId) {
		Page<ShareCapitalManagers> allRecords = shareCapitalManagersRepository.findAll(clientCompanyId, pageable);
		long noOfRecords = allRecords.getTotalElements();
		Result<ShareCapitalManagers> result =
				new Result<>(0, allRecords.getContent(), noOfRecords, pageNumber, pageSize);

		return result;
	}

	@Override
	public ShareCapitalManagers getManagerByNameAndPosition(String name, String position, Long clientCompanyId) {
		return shareCapitalManagersRepository.getManagerByNameAndPosition(name, position, clientCompanyId);
	}
}
