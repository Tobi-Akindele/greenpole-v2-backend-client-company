package com.ap.greenpole.usermodule.config;

import com.ap.greenpole.usermodule.model.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.DefaultResponseErrorHandler;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 20-Sep-20 09:44 PM
 */
@ControllerAdvice
public class ExceptionHandlerConfig extends DefaultResponseErrorHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public GenericResponse<?> handleInternalServerExceptions(Exception ex) {
        ex.printStackTrace();
        return new GenericResponse<>("00", ex.getLocalizedMessage(), null);
    }

}
