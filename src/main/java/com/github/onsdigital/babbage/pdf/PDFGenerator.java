package com.github.onsdigital.babbage.pdf;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.template.TemplateService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.dom4j.DocumentException;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.XMLResource;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;

/**
 * Created by bren on 08/07/15.
 */
public class PDFGenerator {

    private static final String TEMP_DIRECTORY_PATH = FileUtils.getTempDirectoryPath();
    private static final String URL = "http://localhost:8080";

    public static Path generatePdf(String uri, String fileName, Map<String, String> cookies, String pdfTable) {

        try {
            ContentResponse contentResponse = ContentClient.getInstance().getContent(uri);
            String html;
            try (InputStream dataStream = contentResponse.getDataStream()) {
                LinkedHashMap<String, Object> additionalData = new LinkedHashMap<>();
                additionalData.put("pdf_style", true);
                html = TemplateService.getInstance().renderTemplate("pdf/pdf", dataStream, additionalData);

                html = html.replace("\"\"/>", " \"/>"); // img tags from markdown have an extra " at the end of the tag for some reason
                html = Jsoup.parse(html, URL, Parser.xmlParser()).toString();
                html = html.replace("&nbsp;", "&#160;");
            }

            String outputFile = TEMP_DIRECTORY_PATH + "/" + fileName + ".pdf";
            InputStream inputStream = new ByteArrayInputStream(html.getBytes());
            createPDF(uri, inputStream, outputFile);

            Path pdfFile = FileSystems.getDefault().getPath(TEMP_DIRECTORY_PATH).resolve(fileName + ".pdf");
            if (!Files.exists(pdfFile)) {
                throw new RuntimeException("Failed generating pdf, file not created");
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(outputFile));
            addDataTableToPdf(fileName, pdfTable, bufferedReader, pdfFile);

            return pdfFile;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed generating pdf", ex);
        }
    }

    public static void createPDF(String url, InputStream input, String outputFile)
            throws IOException, DocumentException, com.lowagie.text.DocumentException {

        OutputStream os = null;

        try {
            os = new FileOutputStream(outputFile);
            ITextRenderer renderer = new ITextRenderer(4.1666f, 3);

            // Create a chain of custom classes to manipulate the HTML.
            renderer.getSharedContext().setReplacedElementFactory(
                    new HtmlImageReplacedElementFactory(
                            new EquationImageInserter(
                                    new ChartImageReplacedElementFactory(
                                            renderer.getSharedContext().getReplacedElementFactory()))));

            Document doc = XMLResource.load(new InputSource(input)).getDocument();

            renderer.setDocument(doc, url);
            renderer.layout();
            renderer.createPDF(os);

            os.close();
            os = null;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // ignore
                }
            }
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
