package com.ap.greenpole.clientCompanyModule.service;

import com.ap.greenpole.clientCompanyModule.entity.*;
import com.ap.greenpole.clientCompanyModule.utils.ConstantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Lewis.Aguh on 04/10/2020
 */

@Service
public class ProcessShareCapitalManagersService {

    public List<ShareCapitalManagers> processShareCapitalManagers(ClientCompany clientCompany) {
        List<ShareCapitalManagers> managers = new ArrayList<>();


        if(clientCompany.getCeoName() != "" || clientCompany.getCeoName() != null) {
            ShareCapitalManagers manager = new ShareCapitalManagers();
            manager.setName(clientCompany.getCeoName());
            manager.setPosition("CEO");
            managers.add(manager);
        }

        if(clientCompany.getSecretary() != "" || clientCompany.getSecretary() != null) {
            ShareCapitalManagers manager = new ShareCapitalManagers();
            manager.setName(clientCompany.getSecretary());
            manager.setPosition("SECRETARY");
            managers.add(manager);
        }

        if(clientCompany.getDirectors() != null && clientCompany.getDirectors().size() > 0) {
            ShareCapitalManagers manager = new ShareCapitalManagers();
            for(Director director: clientCompany.getDirectors()) {
                manager.setName(director.getFirstName() + ' ' + director.getMiddleName() + ' ' + director.getLastName());
                manager.setPosition("DIRECTOR");
                managers.add(manager);
            }
        }

        return managers;
    }

}
