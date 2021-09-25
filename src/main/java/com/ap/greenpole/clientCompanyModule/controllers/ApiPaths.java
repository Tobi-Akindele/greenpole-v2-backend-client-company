package com.ap.greenpole.clientCompanyModule.controllers;


import static com.ap.greenpole.clientCompanyModule.controllers.ApiParameters.*;

public interface ApiPaths {

    String COMPANY_ID = "/company-id";
    String ALL = "/all";
    String SEARCH = "/search";
    String INTRODUCTION = "/{" + CLIENT_COMPANY_ID + "}/introduction";
    String UPLOAD_IN_MASS = "/{" + CLIENT_COMPANY_ID + "}/in-mass";
    String INVALIDATE = "/invalidate/{"+CLIENT_COMPANY_ID +"}";
    String ATTENDANCE_PATH = "/attendance";
    String SHARE_HOLDER_DOWNLOAD_EXCEL = "/{" + CLIENT_COMPANY_ID + "}/shareholders/download/excel";
    String SHARE_HOLDER_DOWNLOAD_PDF = "/{" + CLIENT_COMPANY_ID + "}/shareholders/download/pdf";
    String SHARE_HOLDER_DOWNLOAD_CSV = "/{" + CLIENT_COMPANY_ID + "}/shareholders/download/csv";
    String SHAREHOLDERS = "/{" + CLIENT_COMPANY_ID + "}/shareholders";
    String TRADE_RIGHTS = "/trade-rights";
    String ALL_SHAREHOLDERS = "/all";
    String INACTIVE = "/inactive";
    String SHAREHOLDERS_BY_BVN = "/bvn/{"+BVN +"}";
    String SHAREHOLDERS_ID = "/{"+SHAREHOLDER_ID +"}";

}
