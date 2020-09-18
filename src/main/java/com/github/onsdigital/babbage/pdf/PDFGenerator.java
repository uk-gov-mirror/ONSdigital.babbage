package com.github.onsdigital.babbage.pdf;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.template.TemplateService;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle;
import com.openhtmltopdf.pdfboxout.PdfBoxRenderer;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.dom4j.DocumentException;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.parser.Parser;
import org.w3c.dom.Document;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;

/**
 * Created by bren on 08/07/15.
 */
public class PDFGenerator {

    private static final String TEMP_DIRECTORY_PATH = FileUtils.getTempDirectoryPath();
    private static final String URL = "http://localhost:8080";

    public static Path generatePdf(String uri, String fileName, Map<String, String> cookies, String pdfTable) {
        try {
            ContentResponse contentResponse = ContentClient.getInstance().getContent(uri);
            Document doc;
            try (InputStream dataStream = contentResponse.getDataStream()) {
                LinkedHashMap<String, Object> additionalData = new LinkedHashMap<>();
                additionalData.put("pdf_style", true);
                String html = TemplateService.getInstance().renderTemplate("pdf/pdf", dataStream, additionalData);
                html = html.replace("\"\"/>", " \"/>"); // img tags from markdown have an extra " at the end of the tag for some reason
                doc = new W3CDom().fromJsoup(Jsoup.parse(html, URL, Parser.xmlParser()));
                // html = html.replace("&nbsp;", "&#160;");
            }

            String outputFile = TEMP_DIRECTORY_PATH + "/" + fileName + ".pdf";
            createPDF(uri, doc, outputFile);

            Path pdfFile = FileSystems.getDefault().getPath(TEMP_DIRECTORY_PATH).resolve(fileName + ".pdf");
            if (!Files.exists(pdfFile)) {
                throw new RuntimeException("Failed generating pdf, file not created");
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(outputFile));
            addDataTableToPdf(fileName, pdfTable, bufferedReader, pdfFile);

            return pdfFile;
        } catch (Exception ex) {
            error().exception(ex)
                    .data("uri", uri)
                    .data("filename", fileName)
                    .log("error generating PDF for uri");
            throw new RuntimeException("Failed generating pdf", ex);
        }
    }

    public static void createPDF(String url, Document doc, String outputFile)
            throws IOException, DocumentException, com.lowagie.text.DocumentException, URISyntaxException {
                
        OutputStream os = null;

        try {
            os = new FileOutputStream(outputFile);
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.usePdfUaAccessbility(true);
            builder.useSVGDrawer(new BatikSVGDrawer());
            builder.useHttpStreamImplementation(new OkHttpStreamFactory());

            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            URL regularFontURL = classloader.getResource("OpenSans-Regular.ttf");
            File regularFontFile = new File(regularFontURL.toURI());
            builder.useFont(regularFontFile, "OpenSans", 400, FontStyle.NORMAL, true);

            URL boldFontURL = classloader.getResource("OpenSans-Bold.ttf");
            File boldFontFile = new File(boldFontURL.toURI());
            builder.useFont(boldFontFile, "OpenSans", 700, FontStyle.NORMAL, true);
            builder.withW3cDocument(doc, url);
            builder.toStream(os);
            try (PdfBoxRenderer renderer = builder.buildPdfRenderer()) {
                // Create a chain of custom classes to manipulate the HTML.
                ChainedReplacedElementFactory cef = new ChainedReplacedElementFactory(renderer.getSharedContext());
                cef.addFactory(new ChartImageReplacedElementFactory(renderer.getOutputDevice()));
                cef.addFactory(new EquationImageInserter(renderer.getOutputDevice()));
                cef.addFactory(new HtmlImageReplacedElementFactory(renderer.getOutputDevice()));

                renderer.getSharedContext().setReplacedElementFactory(cef);
                renderer.layout();
                renderer.createPDF();
            }
        } catch (Exception ex) {
            error().exception(ex)
                    .data("url", url)
                    .data("outputFile", outputFile)
                    .log("error creating PDF");
            throw ex;
        } finally {
            info().log("Done creating pdf: " + outputFile);
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    info().log("Close exception: " +  e);
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
            info().data("commands", ArrayUtils.toString(gsCommand)).log("adding data table to PDF");
            int gsExitStatus = gsProcess.waitFor();

            BufferedReader gsBufferedReader = new BufferedReader(new InputStreamReader(gsProcess.getInputStream()));
            String gsCurrentLine;
            StringBuilder gsStringBuilder = new StringBuilder(gsExitStatus == 0 ? "SUCCESS:" : "ERROR:");
            gsCurrentLine = bufferedReader.readLine();
            while (gsCurrentLine != null) {
                gsStringBuilder.append(gsCurrentLine);
                gsCurrentLine = gsBufferedReader.readLine();
            }

            Path gsPdfFile = FileSystems.getDefault().getPath(TEMP_DIRECTORY_PATH).resolve(fileName + "-merged.pdf");
            if (!Files.exists(gsPdfFile)) {
                throw new RuntimeException("Failed generating pdf, file not created");
            }

            Files.delete(pdfFile);
            Files.move(gsPdfFile, pdfFile);
        }
    }
}
