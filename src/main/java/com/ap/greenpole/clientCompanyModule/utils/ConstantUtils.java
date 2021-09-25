package com.ap.greenpole.clientCompanyModule.utils;

/**
 * Created by Lewis.Aguh on 19/08/2020.
 */

public class ConstantUtils {

	public static final String[] REQUEST_TYPES = {"CREATE_CLIENT_COMPANY", "UPDATE_CLIENT_COMPANY", "DEACTIVATE_CLIENT_COMPANY", "MERGE_CLIENT_COMPANIES", "UPDATE_SHARE_HOLDER", "UPLOAD_ANNUAL_REPORT"};
	public static final String[] APPROVAL_STATUS = {"PENDING_APPROVAL", "APPROVED", "REJECTED"};
	public static final String[] APPROVAL_ACTIONS = {"APPROVE", "REJECT"};
	public static final String MODULE_APPROVAL_PERMISSION = "CLIENT_COMPANY_APPROVAL";
	public static final String AUTHORIZATION = "Authorization";

	public static final String EXPORT_DIRECTORY = "/ClientCompany/Exports/";
	public static final String CLIENT_COMPANY_EXPORT = "CLIENT_COMPANY_EXPORT";
	public static final String ANNUAL_REPORT_DIRECTORY = "/ClientCompany/AnnualReports/";
	public static final String ANNUAL_GENERAL_REPORT = "ANNUAL_GENERAL_REPORT";
	public static final String[] SUPPORTED_EXPORT_FORMATS = {"CSV", "XLSX", "PDF"};
	public static final int PENDING = 1;
	public static final int REJECTED = 2;
	public static final int APPROVED = 3;
	public static final String MODULE = "CLIENT_COMPANY";
	public static final String FILTER_DATE_FORMAT = "yyyy-MM-dd";
	public static final String FILTER_DATE= "yyyy-MM-dd.HH";
	public static final String[] VOTING_OPTIONS = {"AGREE", "DISAGREE"};
	public static final String[] VOTING_PROCESS_STATUS = {"ACTIVE", "COMPLETED", "CANCELLED"};

	public static final String[] SHARE_CAPITAL_HEADER_NAMES = {"CLIENT_COMPANY_ID", "REGISTER_NAME", "REGISTER_CODE", "AUTHORISED_SHARE_CAPITAL", "PAID_UP_SHARE_CAPITAL"};

}
