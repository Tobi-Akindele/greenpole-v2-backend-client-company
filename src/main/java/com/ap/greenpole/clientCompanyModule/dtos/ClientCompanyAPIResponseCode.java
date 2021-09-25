package com.ap.greenpole.clientCompanyModule.dtos;

/**
 * Created by Lewis.Aguh on 08/10/2020
 */
public enum ClientCompanyAPIResponseCode {

    SUCCESSFUL("00"), FAILED("01");

    private String status;

    ClientCompanyAPIResponseCode(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
