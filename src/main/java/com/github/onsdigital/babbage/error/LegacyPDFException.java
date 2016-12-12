package com.github.onsdigital.babbage.error;

/**
 * Temp exception specifically for legacy PDF requests. will be removed once actual fix is in.
 */
public class LegacyPDFException extends BabbageException {

    private static final String PDF_ERROR_MESSAGE = "We have a known issue with out PDF generation....";

    public LegacyPDFException() {
        super(501, PDF_ERROR_MESSAGE);
    }
}
