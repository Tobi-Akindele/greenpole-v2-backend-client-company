package com.ap.greenpole.clientCompanyModule.controllers;

import com.ap.greenpole.clientCompanyModule.entity.Shareholder;
import com.ap.greenpole.clientCompanyModule.service.ShareHolderService;
import com.ap.greenpole.usermodule.annotation.PreAuthorizePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ap.greenpole.clientCompanyModule.controllers.ApiParameters.BVN;
import static com.ap.greenpole.clientCompanyModule.controllers.ApiParameters.SHAREHOLDER_ID;
import static com.ap.greenpole.clientCompanyModule.controllers.util.EntityMapper.mapShareHoldersToCircularRightsDtos;

@RestController
@RequestMapping(path= "/api/v1/client/shareholder", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class ShareholderController {

    @Autowired
    ShareHolderService shareHolderService;

    @RequestMapping(path = ApiPaths.ALL_SHAREHOLDERS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_SHAREHOLDER"})
    public ResponseEntity<?> getAllActiveShareHolders(){
        List<Shareholder> shareholders = shareHolderService.getActiveShareHolders();
        return new ResponseEntity<>(shareholders, HttpStatus.OK);
    }

    @RequestMapping(path = ApiPaths.INACTIVE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_SHAREHOLDER"})
    public ResponseEntity<?> getAllInActiveShareHolders(){
        List<Shareholder> shareholders = shareHolderService.getInActiveShareHolders();
        return new ResponseEntity<>(shareholders, HttpStatus.OK);
    }

    @RequestMapping(path = ApiPaths.SHAREHOLDERS_ID, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_SHAREHOLDER"})
    public ResponseEntity<?> getShareholderById(@PathVariable(name = SHAREHOLDER_ID) long shareholderId){
        Shareholder shareholder = shareHolderService.findById(shareholderId);
        return new ResponseEntity<>(shareholder, HttpStatus.OK);
    }


    @RequestMapping(path = ApiPaths.SHAREHOLDERS_BY_BVN, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_SHAREHOLDER"})
    public ResponseEntity<?> getShareHolderByBvn(@PathVariable(name = BVN) String bvn){
        List<Shareholder> shareholderAccount = shareHolderService.findByBvn(bvn);
        if (shareholderAccount.isEmpty()){
            return new ResponseEntity<>("Shareholder with BVN " + bvn+ " does not exist", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(mapShareHoldersToCircularRightsDtos(shareholderAccount), HttpStatus.OK);
    }
}
