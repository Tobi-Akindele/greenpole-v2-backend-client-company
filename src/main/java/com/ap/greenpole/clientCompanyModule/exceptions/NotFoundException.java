package com.ap.greenpole.clientCompanyModule.exceptions;

/**
 * Created by Lewis.Aguh on 19/08/2020.
 */

public class NotFoundException extends AbstractException {
	private static final long serialVersionUID = 1L;
    public NotFoundException(String code, String message) {
        super(code, message);
    }
}
