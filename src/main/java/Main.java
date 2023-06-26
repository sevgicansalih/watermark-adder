import com.google.common.base.CaseFormat;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

public class Main {
    // dr mu hemşire mi ona göre kod çalıştır.
    private static final String ISIM_ONEK = "Dr. ";

    // pdf dosya ismi ve csv dosya ismi
    private static final String FILENAME = "temel";
    private static final String DATA_GUEST_LIST = "kasım_liste.csv";

    // csv dosyasindaki sutun isimleri
    private static final String NAME_COLUMN_NAME = "Guest name";
    private static final String TC_COLUMN_NAME = "TC Kimlik Numaranız";

    // PDF Editleme Sifresi
    private static final String OWNER_PASSWORD = "123";

    // watermark textleri
    private static final String DATE_OF_MEETING = "25 Ekim 2022";
    private static final String GROUP_NAME = "Test Grubu";
    private static final String HEADER_LEFT = DATE_OF_MEETING;
    private static final String HEADER_RIGHT = GROUP_NAME;
    private static final String FOOTER_RIGHT_PREFIX = "Sayfa ";

    // watermark Font size
    private static final int WATERMARK_FONT_SIZE = 36;
    private static final int HEADER_LEFT_FONT_SIZE = 16;
    private static final int HEADER_RIGHT_FONT_SIZE = 16;
    private static final int FOOTER_RIGHT_FONT_SIZE = 16;

    private static final String PDF_FONT = "./src/main/resources/font/arial_font.ttf";

    private static final String SOURCE_PATH = "C:\\Users\\Cihan\\OneDrive\\Belgeler\\AKTİF\\sevgicanakademi\\şirket işleri\\kayıt masası\\kasım kayıt\\hekim_127_salih\\" + FILENAME + ".pdf";
    private static final String OUTPUT_PATH = "C:\\Users\\Cihan\\OneDrive\\Belgeler\\AKTİF\\sevgicanakademi\\şirket işleri\\kayıt masası\\kasım kayıt\\hekim_127_salih\\output";
    private static final String GUEST_LIST_PATH = "C:\\Users\\Cihan\\OneDrive\\Belgeler\\AKTİF\\sevgicanakademi\\şirket işleri\\kayıt masası\\kasım kayıt\\hekim_127_salih\\" + DATA_GUEST_LIST;

    // Salihin Test alani
//    private static final String SOURCE_PATH = "/Users/sevgican/IdeaProjects/watermark-adder/" + FILENAME + ".pdf";
//    private static final String OUTPUT_PATH = "/Users/sevgican/IdeaProjects/watermark-adder/output";
//    private static final String GUEST_LIST_PATH = "/Users/sevgican/IdeaProjects/watermark-adder/" + DATA_GUEST_LIST;


    public static void main(String[] args) {
        //doPdfModificationWork();
//        createGUI();
        new MySwingApp();
    }

    private static void createGUI() {
        JFrame frame = new JFrame("My First GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,500);

        // 8 JPanels

        /*
         * First panel 2 radio buttons, 1 JTextField, 1 JFileChooser
         */
        JPanel firstPanel = GUI.getFirstPanel();
        /*
         * Second panel 1 JLabel, 2 JFormattedTextField
         */

        /*
         * Third panel 1 JLabel, 2 RadioButtons
         */


        // second section
        frame.add(firstPanel); // Adds Button to content pane of frame
        frame.setVisible(true);
    }

    private static void doPdfModificationWork() {
        try {
            // reads CSV
            List<String[]> table = readCSV();
            // Find Table Headers
            List<String> tableHeaders = Arrays.asList(table.get(0));
            int nameColumnIdx = tableHeaders.indexOf(NAME_COLUMN_NAME);
            int tcColumnIdx = tableHeaders.indexOf(TC_COLUMN_NAME);
            // For loop traverses each Row one by one
            for (int rowIdx = 1; rowIdx < table.size(); rowIdx++) {
                List<String> row = Arrays.asList(table.get(rowIdx));
                String guestName = row.get(nameColumnIdx);
                String guestTc = row.get(tcColumnIdx);

                // Open original pdf document
                PdfDocument pdfDocument = getPdfDocument(getDestinationPath(guestName), guestTc);
                processDocument(pdfDocument, ISIM_ONEK + getCamelCaseFormat(guestName), guestTc);
                pdfDocument.close();
            }
        } catch (Exception e) {
            System.out.println("Error!: " + e.getMessage());
        }
    }

    private static void processDocument(PdfDocument pdfDocument, String guestName, String guestTc) throws Exception {
        addHeaderAndFooter(pdfDocument);
        addWatermark(pdfDocument, guestName);
    }

    private static List<String[]> readCSV() throws Exception {
        final String fileCharset = "UTF-16";
        final Reader reader = new InputStreamReader(new FileInputStream(GUEST_LIST_PATH), fileCharset);

        CSVParser parser = new CSVParserBuilder()
                .withSeparator('\t')
                .build();

        CSVReader cr = new CSVReaderBuilder(reader)
                .withSkipLines(0)
                .withCSVParser(parser)
                .build();

        return cr.readAll();
    }

    private static PdfDocument getPdfDocument(String destinationPath, String guestTc) throws Exception {
        log("File: " + new File(destinationPath).getName() + " Psswd: " + guestTc);
        return new PdfDocument(new PdfReader(SOURCE_PATH),
                new PdfWriter(destinationPath,
                        new WriterProperties()
                                .setStandardEncryption(
                                        guestTc.getBytes(),
                                        OWNER_PASSWORD.getBytes(),
                                        0,
                                        EncryptionConstants.ENCRYPTION_AES_256)
                )
        );
    }

    private static void addHeaderAndFooter(PdfDocument pdfDoc) throws Exception {
        Document doc = new Document(pdfDoc);

        Paragraph headerLeft = new Paragraph(HEADER_LEFT)
                .setFont(PdfFontFactory.createFont(PDF_FONT))
                .setFontSize(HEADER_LEFT_FONT_SIZE);

        Paragraph headerRight = new Paragraph(HEADER_RIGHT)
                .setFont(PdfFontFactory.createFont(PDF_FONT))
                .setFontSize(HEADER_RIGHT_FONT_SIZE);

        Paragraph footer;

        for (int i = 3; i <= pdfDoc.getNumberOfPages() - 1; i++) {
            Rectangle pageSize = pdfDoc.getPage(i).getPageSize();
            // show header left
            float positionX = 120;
            float positionY = pageSize.getTop() - 40;
            doc.showTextAligned(headerLeft, positionX, positionY, i, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);

            //show header right
            positionX = pageSize.getWidth() - 100;
            positionY = pageSize.getTop() - 40;
            doc.showTextAligned(headerRight, positionX, positionY, i, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);

            //show footer
            footer = new Paragraph(FOOTER_RIGHT_PREFIX + (i - 2))
                    .setFont(PdfFontFactory.createFont(PDF_FONT))
                    .setFontSize(FOOTER_RIGHT_FONT_SIZE);

            positionX = pageSize.getWidth() - 100;
            positionY = pageSize.getBottom() + 40;
            doc.showTextAligned(footer, positionX, positionY, i, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);
        }
    }

    protected static void addWatermark(PdfDocument pdfDoc, String text) throws Exception {
        PdfFont font = PdfFontFactory.createFont(PDF_FONT);

        Paragraph paragraph = new Paragraph(text)
                .setFont(font)
                .setFontSize(WATERMARK_FONT_SIZE);

        PdfExtGState gs1 = new PdfExtGState().setFillOpacity(0.2f);

        for (int i = 3; i <= pdfDoc.getNumberOfPages() - 1; i++) {
            PdfPage pdfPage = pdfDoc.getPage(i);
            Rectangle pageSize = pdfPage.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
            pdfCanvas.setExtGState(gs1);
            float x = pageSize.getWidth() / 2;
            float y = pageSize.getHeight() / 2;
            float radAngle;
            if (pdfPage.getRotation() % 180 == 0) {
                radAngle = 45;
            } else {
                radAngle = 45;
            }
            Canvas canvas = new Canvas(pdfPage, pageSize).showTextAligned(paragraph, x, y, i, TextAlignment.CENTER, VerticalAlignment.TOP, radAngle);
            canvas.close();
        }
    }

    private static void log(String message) {
        System.out.println(message);
    }

    private static String getDestinationPath(String guestName) throws Exception {
        String camelCase = getCamelCaseFormat(guestName);
        File file = new File(OUTPUT_PATH);
        if (!file.exists()) {
            boolean isCreated = file.mkdir();
            if (!isCreated) {
                throw new Exception("Directory cannot be created!");
            }
        }
        return OUTPUT_PATH + "/" + FILENAME + "_" + camelCase + ".pdf";
    }

    private static String getCamelCaseFormat(String name) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name.replace(" ", "_"));
    }
}