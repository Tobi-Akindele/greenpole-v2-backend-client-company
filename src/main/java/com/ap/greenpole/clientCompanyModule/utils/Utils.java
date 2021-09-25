package com.ap.greenpole.clientCompanyModule.utils;

import com.ap.greenpole.clientCompanyModule.entity.ModuleRequest;
import com.ap.greenpole.clientCompanyModule.exceptions.BadRequestException;
import com.ap.greenpole.clientCompanyModule.service.ClientCompanyRequestService;
//import com.sun.deploy.net.HttpResponse;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static com.ap.greenpole.clientCompanyModule.utils.Const.DATE_FORMATE;

/**
 * Created by Lewis.Aguh on 19/08/2020.
 */

public class Utils {

	private static Logger logger = LoggerFactory.getLogger(Utils.class);

	public static boolean isEmptyString(String str) {
		return str == null || str.equalsIgnoreCase("") || str.equalsIgnoreCase("null");
	}
	
	public static boolean isValidEmails(List<String> emails) {
		boolean status = false;
		List<Boolean> stat = new ArrayList<>();
		if (emails != null && !emails.isEmpty()) {

			for (String e : emails) {
				if (!Utils.isEmptyString(e)) {
					boolean s = isValidEmail(e) ? true : false;
					stat.add(s);
				}
			}
			status = (!stat.isEmpty() && stat.contains(false)) ? false : true;
		}

		return status;
	}

	public static boolean isValidDateFormat(String format, String value, Locale locale) {
		DateTimeFormatter fomatter = DateTimeFormatter.ofPattern(format, locale);

		try {
			LocalDateTime ldt = LocalDateTime.parse(value, fomatter);
			String result = ldt.format(fomatter);
			return result.equals(value);
		} catch (DateTimeParseException e) {
			try {
				LocalDate ld = LocalDate.parse(value, fomatter);
				String result = ld.format(fomatter);
				return result.equals(value);
			} catch (DateTimeParseException exp) {
				try {
					LocalTime lt = LocalTime.parse(value, fomatter);
					String result = lt.format(fomatter);
					return result.equals(value);
				} catch (DateTimeParseException e2) {
					// Debugging purposes
					logger.error(e2.getMessage());
				}
			}
		}
		return false;
	}

	public static boolean isValidEmail(String email) {
		String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		return email.matches(EMAIL_REGEX);
	}
	
	public static ModuleRequest checkForOngoingApproval(ClientCompanyRequestService clientCompanyRequestService,
														long clientComanyId, String requestType) {
		ModuleRequest moduleRequest = new ModuleRequest();
		moduleRequest.setResourceId(clientComanyId);
		moduleRequest.setActionRequired(requestType);
		ModuleRequest request = clientCompanyRequestService.getApprovalRequestByResourceIdAndActionRequired(moduleRequest);
		if(request != null && ConstantUtils.PENDING == request.getStatus())
			throw new BadRequestException("400", "There is an ongoing " + requestType + " Approval process");
		return moduleRequest;
	}
	
	public static List<String> commaSeperatedToList(String src){
		List<String> result = new ArrayList<>();
		if(!isEmptyString(src)) {
			result = Arrays.asList(StringUtils.split(src, ","));
		}
		return result;
	}

	public static String getDateString(Date date) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
		sdfDate.format(0);
		String strDate = sdfDate.format(date);
		return strDate;
	}

	static String splitCamelCase(String s) {
		return s.replaceAll(
				String.format("%s|%s|%s",
						"(?<=[A-Z])(?=[A-Z][a-z])",
						"(?<=[^A-Z])(?=[A-Z])",
						"(?<=[A-Za-z])(?=[^A-Za-z])"
				),
				" "
		);
	}

	public static boolean isValidDate(String format, String date) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		try {
			LocalDateTime ldt = LocalDateTime.parse(date, formatter);
			String result = ldt.format(formatter);

			return result.equals(date);
		}catch(DateTimeParseException ex) {
			try {
				LocalDate ldt = LocalDate.parse(date, formatter);
				String result = ldt.format(formatter);

				return result.equals(date);
			}catch(DateTimeParseException exp) {
				try {
					LocalTime ldt = LocalTime.parse(date, formatter);
					String result = ldt.format(formatter);

					return result.equals(date);
				}catch(DateTimeParseException exp1) {
					logger.info("Unable to parse date: {}. Error: {}", date, exp1);
				}
			}
		}
		return false;
	}

	public static Date getDate(String format, String input) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = (Date) dateFormat.parse(input);
		}catch(ParseException ex) {
			logger.info("Unable to parse date: {}, Error: {}", input, ex);
		}
		return date;
	}

	public static Date getDateInMilli(String input){
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd.HH.mm.ss");
		Date date = null;
		try {
			date = dateFormat.parse(input);
		}catch(ParseException ex) {
			logger.info("Unable to parse date: {}, Error: {}", input, ex);
		}
		return date;
	}
}
