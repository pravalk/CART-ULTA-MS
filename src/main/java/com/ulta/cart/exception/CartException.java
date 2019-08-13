package com.ulta.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author BrijendraK
 *
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CartException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param exception
     */
    public CartException(String exception) {
        super(exception);
    }

}