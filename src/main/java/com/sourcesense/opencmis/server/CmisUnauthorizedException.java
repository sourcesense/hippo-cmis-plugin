package com.sourcesense.opencmis.server;

import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;

import java.math.BigInteger;

public class CmisUnauthorizedException extends CmisBaseException {

    private static final long serialVersionUID = 1L;

    public CmisUnauthorizedException() {
        super();
    }

    public CmisUnauthorizedException(String message, BigInteger code, Throwable cause) {
        super(message, code, cause);
    }

    public CmisUnauthorizedException(String message, String errorContent) {
        super(message, errorContent);
    }

    public CmisUnauthorizedException(String message, BigInteger code) {
        super(message, code);
    }

    public CmisUnauthorizedException(String message, String errorContent, Throwable cause) {
        super(message, errorContent, cause);
    }

    public CmisUnauthorizedException(String message, Throwable cause) {
        super(message, BigInteger.ZERO, cause);
    }

    public CmisUnauthorizedException(String message) {
        super(message, BigInteger.ZERO);
    }
}
