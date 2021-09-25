package com.ap.greenpole.clientCompanyModule.service.impl;

import com.ap.greenpole.clientCompanyModule.entity.ClientCompany;
import com.ap.greenpole.clientCompanyModule.entity.Shareholder;
import com.ap.greenpole.clientCompanyModule.enums.GenericStatusEnum;
import com.ap.greenpole.clientCompanyModule.repositories.ShareHolderRepository;
import com.ap.greenpole.clientCompanyModule.service.ShareHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShareHolderServiceImpl implements ShareHolderService {

    @Autowired
    ShareHolderRepository shareHolderRepository;

    @Override
    public List<Shareholder> findByClientCompany(ClientCompany clientCompany) {
        return shareHolderRepository.findAllByClientCompany(clientCompany.getId(),GenericStatusEnum.ACTIVE.name());
    }

    @Override
    public Shareholder findById(long id) {
        return shareHolderRepository.findByShareholder_id(id,GenericStatusEnum.ACTIVE.name());
    }

    @Override
    public List<Shareholder> getActiveShareHolders() {
        return shareHolderRepository.findByStatus(GenericStatusEnum.ACTIVE.name());
    }

    @Override
    public List<Shareholder> getInActiveShareHolders() {
        return shareHolderRepository.findByStatus(GenericStatusEnum.INACTIVE.name());
    }

    @Override
    public List<Shareholder> findByBvn(String bvn) {
        return shareHolderRepository.findByBvnandAndStatusOrderByCreatedOnDesc(bvn,GenericStatusEnum.ACTIVE.name());
    }

}
