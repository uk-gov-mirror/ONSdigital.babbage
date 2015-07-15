package com.github.onsdigital.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by bren on 08/07/15.
 */
public class PDFGeneratorTest {

    @Test
    public void testGeneratePdf() throws Exception {
        PDFGenerator.generatePdf("/businessindustryandtrade/changestobusiness/mergersandacquisitions/bulletins/mergersandacquisitionsinvolvingukcompanies/2015-06-02", "mypdf");
    }
}