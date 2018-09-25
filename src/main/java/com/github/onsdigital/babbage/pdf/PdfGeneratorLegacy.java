package com.github.onsdigital.babbage.pdf;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

import static com.github.onsdigital.babbage.configuration.AppConfiguration.appConfig;

public class PdfGeneratorLegacy {

    private static final String TEMP_DIRECTORY_PATH = FileUtils.getTempDirectoryPath();
    private static final String URL = "http://localhost:8080";

    public static Path generatePdf(String uri, String fileName, Map<String, String> cookies, String pdfTable) {
        String[] command = {
                appConfig().babbage().getPhantomjsPath(), "target/web/js/generatepdf.js",
                URL + uri + "?pdf=1", "" + TEMP_DIRECTORY_PATH + "/" + fileName + ".pdf"
        };

        Iterator<Map.Entry<String, String>> iterator = cookies.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            command = ArrayUtils.add(command, next.getKey());
            command = ArrayUtils.add(command, next.getValue());
        }
        try {
            // Execute command, redirect error to output to print all in the console
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

            addDataTableToPdf(fileName, pdfTable, bufferedReader, pdfFile);

            return pdfFile;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed generating pdf", ex);
        }
    }

    private static void addDataTableToPdf(String fileName, String pdfTable, BufferedReader bufferedReader, Path pdfFile) throws IOException, InterruptedException {
        if (pdfTable != null) {
            String[] gsCommand = {
                    appConfig().babbage().getGhostscriptPath(),
                    "-dBATCH", "-dNOPAUSE", "-q", "-sDEVICE=pdfwrite", "-dPDFSETTINGS=/prepress",
                    "-sOutputFile=" + TEMP_DIRECTORY_PATH + "/" + fileName + "-merged.pdf",
                    TEMP_DIRECTORY_PATH + "/" + fileName + ".pdf", pdfTable
            };

            Process gsProcess = new ProcessBuilder(gsCommand).redirectErrorStream(true).start();
            System.out.println(ArrayUtils.toString(gsCommand));
            int gsExitStatus = gsProcess.waitFor();

            BufferedReader gsBufferedReader = new BufferedReader(new InputStreamReader(gsProcess.getInputStream()));
            String gsCurrentLine;
            StringBuilder gsStringBuilder = new StringBuilder(gsExitStatus == 0 ? "SUCCESS:" : "ERROR:");
            gsCurrentLine = bufferedReader.readLine();
            while (gsCurrentLine != null) {
                gsStringBuilder.append(gsCurrentLine);
                gsCurrentLine = gsBufferedReader.readLine();
            }
            System.out.println(gsStringBuilder.toString());

            Path gsPdfFile = FileSystems.getDefault().getPath(TEMP_DIRECTORY_PATH).resolve(fileName + "-merged.pdf");
            if (!Files.exists(gsPdfFile)) {
                throw new RuntimeException("Failed generating pdf, file not created");
            }

            Files.delete(pdfFile);
            Files.move(gsPdfFile, pdfFile);

        }
    }
}
