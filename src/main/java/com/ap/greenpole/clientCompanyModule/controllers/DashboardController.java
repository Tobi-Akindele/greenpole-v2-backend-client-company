package com.ap.greenpole.clientCompanyModule.controllers;

import com.ap.greenpole.clientCompanyModule.dtos.DashboardDto;
import com.ap.greenpole.clientCompanyModule.repositories.ClientCompanyRepository;
import com.ap.greenpole.clientCompanyModule.service.ClientCompanyService;
import com.ap.greenpole.clientCompanyModule.service.ShareHolderService;
import com.ap.greenpole.clientCompanyModule.utils.ConstantUtils;
import com.ap.greenpole.usermodule.annotation.PreAuthorizePermission;
import com.ap.greenpole.usermodule.model.User;
import com.ap.greenpole.usermodule.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping(path= "/api/v1/dashboard")
public class DashboardController {

    private static Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private UserService userService;

    @Autowired
    ClientCompanyService clientCompanyService;

    @Autowired
    ClientCompanyRepository clientCompanyRepository;

    @Autowired
    ShareHolderService shareHolderService;

    @Transactional
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_VIEW_DASHBOARD"})
    public ResponseEntity<?> dashboard(HttpServletRequest request){
        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        int activeClientCompanies = clientCompanyService.getAllClientCompanies().size();
        int inactiveClientCompany = clientCompanyService.getAllInactiveClientCompanies().size();
        Long totalClientCompanies = clientCompanyRepository.count();
        int inactiveShareholders = shareHolderService.getInActiveShareHolders().size();
        int activeShareholders = shareHolderService.getActiveShareHolders().size();

        DashboardDto dto = new DashboardDto();
        dto.setActiveClientCompanyCount(activeClientCompanies);
        dto.setInActiveClientCompanyCount(inactiveClientCompany);
        dto.setActiveShareHoldersCount(activeShareholders);
        dto.setInactiveShareHoldersCount(inactiveShareholders);
        dto.setClientCompanyCount(totalClientCompanies);

        return new ResponseEntity<>(dto, HttpStatus.OK);

    }

}
