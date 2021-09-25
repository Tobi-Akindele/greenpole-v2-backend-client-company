package com.ap.greenpole.clientCompanyModule.service;

import com.ap.greenpole.clientCompanyModule.dtos.ShareHolderInMassRequestDto;

import java.util.ArrayList;

public class ShareHolderInMassList {

    private ArrayList<ShareHolderInMassRequestDto> shareHolderInMassRequestDtos = new ArrayList<>();

    public ArrayList<ShareHolderInMassRequestDto> getShareHolderInMassRequestDtos() {
        return shareHolderInMassRequestDtos;
    }

    public void setShareHolderInMassRequestDtos(ArrayList<ShareHolderInMassRequestDto> shareHolderInMassRequestDtos) {
        this.shareHolderInMassRequestDtos = shareHolderInMassRequestDtos;
    }
}
