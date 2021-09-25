package com.ap.greenpole.clientCompanyModule.dtos;

import com.ap.greenpole.clientCompanyModule.entity.ModuleRequest;
import com.ap.greenpole.clientCompanyModule.utils.Const;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class GenericMapper implements Serializable {


    public static Object MapJsonStringToRequest(ModuleRequest request){

        Gson gson = new GsonBuilder().setDateFormat(Const.DATE_FORMATE).create();

        if (request.getActionRequired().equalsIgnoreCase(Const.CREATE)){
            return gson.fromJson(request.getNewRecord(),ClientCompanyRequestDto.class);
        }
        if (request.getActionRequired().equalsIgnoreCase(Const.TRADE_RIGHTS)){
            return gson.fromJson(request.getNewRecord(),TradeRightsDto.class);
        }
        if (request.getActionRequired().equalsIgnoreCase(Const.CREATE_SHAREHOLDER_INTRODUCTION)){
        return gson.fromJson(request.getNewRecord(), ShareHolderRequestDto[].class);
        }
        if (request.getActionRequired().equalsIgnoreCase(Const.CREATE_SHAREHOLDER_IN_MASS)){
            return gson.fromJson(request.getNewRecord(), ShareHolderInMassRequestDto[].class);
        }
        if (request.getActionRequired().equalsIgnoreCase(Const.INVALIDATE)){
            return gson.fromJson(request.getOldRecord(),ClientCompanyResponseDto.class);
        }
        return null;
    }
}
