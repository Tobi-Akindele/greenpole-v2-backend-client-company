package com.ap.greenpole.clientCompanyModule.controllers.util;

public enum StatusMapper {

    PENDING(1), REJECTED(2), APPROVED(3);

    int status;

    StatusMapper(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
