package com.github.onsdigital.util;

import com.github.onsdigital.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * Created by bren on 08/07/15.
 */
public class PDFGenerator {

    private static final String TEMP_DIRECTORY_PATH = FileUtils.getTempDirectoryPath();
        private static final String URL = "http://localhost:8080";
    //Phantom js export code

    public static Path generatePdf(String uri, String fileName) {
        Runtime rt = Runtime.getRuntime();
        // Execute command, redirect error to output to print all in the console
        String[] command = {
                Configuration.PHANTOMJS.getPhantomjsPath(), "src/main/web/js/generatepdf.js", URL + uri, "" + TEMP_DIRECTORY_PATH + "/" + fileName + ".pdf"
        };
        try {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            System.out.println(ArrayUtils.toString(command));
            int exitStatus = process.waitFor();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String currentLine;
            StringBuilder stringBuilder = new StringBuilder(exitStatus == 0 ? "SUCCESS:" : "ERROR:");
            currentLine = bufferedReader.readLine();
            while (currentLine != null) {
                stringBuilder.append(currentLine);
                currentLine = bufferedReader.readLine();
            }
            System.out.println(stringBuilder.toString());
            Path pdfFile = FileSystems.getDefault().getPath(TEMP_DIRECTORY_PATH).resolve(fileName + ".pdf");
            if (!Files.exists(pdfFile)) {
                throw new RuntimeException("Failed generating pdf, file not created");
            }
            return pdfFile;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed generating pdf", ex);
        }
    }
}
