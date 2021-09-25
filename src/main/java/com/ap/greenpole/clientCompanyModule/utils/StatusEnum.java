package com.ap.greenpole.clientCompanyModule.utils;

public enum StatusEnum {

    PENDING(1),
    REJECTED(2),
    APPROVED(3);

    int status;

    StatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
