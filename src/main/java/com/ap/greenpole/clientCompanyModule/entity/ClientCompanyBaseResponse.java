package com.ap.greenpole.clientCompanyModule.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Created by Lewis.Aguh on 08/10/2020
 */

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientCompanyBaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String HttpStatus;

    private String status;

    private String statusMessage;

    private  T data;


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHttpStatus() {
        return HttpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        HttpStatus = httpStatus;
    }
}
