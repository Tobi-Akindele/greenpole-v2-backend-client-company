package com.ap.greenpole.clientCompanyModule.enums;

public enum StatusEnum {

    PENDING(1), REJECTED(2), APPROVED(3);

    StatusEnum(int status) {
        this.status = status;
    }

    private int status;

    public int getStatus() {
        return status;
    }
}
