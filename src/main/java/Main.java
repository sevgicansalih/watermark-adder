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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

public class Main {

    // csv dosyasindaki sutun isimleri
    private static final String NAME_COLUMN_NAME = "Guest name";
    private static final String TC_COLUMN_NAME = "TC Kimlik Numaranız";

    // PDF Editleme Sifresi
    private static final String OWNER_PASSWORD = "123";

    // watermark textleri
    private static final String FOOTER_RIGHT_PREFIX = "Sayfa ";

    // watermark Font size
    private static final int WATERMARK_FONT_SIZE = 36;
    private static final int HEADER_FONT_SIZE = 16;
    private static final int FOOTER_RIGHT_FONT_SIZE = 16;

    private static final String PDF_FONT = "./src/main/resources/font/arial_font.ttf";


    private static final String TEMPLATE_INSTRUCTIONS_PATH = "watermark_dosyalar/degiskenler.csv";
    private static final String COLUMN_COURSE_OWNER = "Kimin_Dersi";
    private static final String COLUMN_ONEK = "Onek";
    private static final String COLUMN_COURSE_NAME = "Ders_Adi";
    private static final String COLUMN_DATE = "Tarih";
    private static final String COLUMN_NO = "No";
    private static final String COLUMN_PDF_SOURCE_PATH = "PDF_Lokasyon";
    private static final String COLUMN_GUESTLIST_SOURCE_PATH = "Katilimci_Lokasyon";
    private static final String COLUMN_OUTPUT_DIRECTORY_NAME = "Output_Lokasyon";
    private static final String COLUMN_SHOULD_PROCESS = "Hangisini_Uret";
    private static final String NO_PREFIX = "No: ";
    private static final String PATH_SEPERATOR_LINUX = "/";
    private static final String PATH_SEPERATOR_WINDOWS = "\\";
    private static final String PATH_SEPERATOR_CURRENT = PATH_SEPERATOR_LINUX;
    private static final boolean IS_PASSWORD_ENCRYPTION_ENABLED = false;


    public static void main(String[] args) {
        doMain();
    }

    private static void doMain() {
        try {
            String pathToFolder = new File(TEMPLATE_INSTRUCTIONS_PATH).getParentFile().getAbsolutePath();
            System.out.println("Reading instructions... path: "+ pathToFolder);
            List<String[]> instructionsTable = readTemplateInstructionsCSV();
            System.out.println("Instructions read is completed. Size: "+ instructionsTable.size());
            List<String> instructionsTableHeaders = Arrays.asList(instructionsTable.get(0));
            int courseOwnerIndex = instructionsTableHeaders.indexOf(COLUMN_COURSE_OWNER);
            int onekIndex = instructionsTableHeaders.indexOf(COLUMN_ONEK);
            int courseNameIndex = instructionsTableHeaders.indexOf(COLUMN_COURSE_NAME);
            int dateIndex = instructionsTableHeaders.indexOf(COLUMN_DATE);
            int noIndex = instructionsTableHeaders.indexOf(COLUMN_NO);
            int pdfPathIndex = instructionsTableHeaders.indexOf(COLUMN_PDF_SOURCE_PATH);
            int guestlistPathIndex = instructionsTableHeaders.indexOf(COLUMN_GUESTLIST_SOURCE_PATH);
            int outputPathIndex = instructionsTableHeaders.indexOf(COLUMN_OUTPUT_DIRECTORY_NAME);
            int shouldProcessIndex = instructionsTableHeaders.indexOf(COLUMN_SHOULD_PROCESS);
            for (int instructionRowIdx = 1; instructionRowIdx < instructionsTable.size(); instructionRowIdx++) {
                System.out.println("Starting Instruction Row Idx: " + instructionRowIdx);
                List<String> instructionRow = Arrays.asList(instructionsTable.get(instructionRowIdx));
                String shouldProcess = instructionRow.get(shouldProcessIndex);
                String courseOwner = instructionRow.get(courseOwnerIndex);
                String onek = instructionRow.get(onekIndex).trim();
                String courseName = instructionRow.get(courseNameIndex);
                String date = instructionRow.get(dateIndex);
                String no = instructionRow.get(noIndex);
                if (shouldSkipInstructionRow(shouldProcess)) {
                    System.out.println("Skipping course: \""+courseName+"\" from: "+courseOwner);
                    continue;
                }


                no = NO_PREFIX + no;
                String pdfPath = instructionRow.get(pdfPathIndex);
                pdfPath = pathToFolder + PATH_SEPERATOR_CURRENT + pdfPath;
                String guestlistPath = instructionRow.get(guestlistPathIndex);
                guestlistPath = pathToFolder + PATH_SEPERATOR_CURRENT + guestlistPath;
                String outputPath = instructionRow.get(outputPathIndex);
                outputPath = pathToFolder + PATH_SEPERATOR_CURRENT + outputPath;
                System.out.println("Instruction row is read. Now will read guestlist");

                // reads CSV
                List<String[]> guestlistTable = readGuestListCSV(guestlistPath);
                System.out.println("GuestTable is read. Size: " + guestlistTable.size());
                // Find Table Headers
                List<String> tableHeaders = Arrays.asList(guestlistTable.get(0));
                int nameColumnIdx = tableHeaders.indexOf(NAME_COLUMN_NAME);
                int tcColumnIdx = tableHeaders.indexOf(TC_COLUMN_NAME);
                // For loop traverses each Row one by one
                for (int rowIdx = 1; rowIdx < guestlistTable.size(); rowIdx++) {
                    List<String> row = Arrays.asList(guestlistTable.get(rowIdx));
                    String guestName = row.get(nameColumnIdx);
                    String guestTc = row.get(tcColumnIdx);

                    String guestNameForDoc = getCamelCaseFormat(guestName);
                    if (onek.length() > 0) {
                        guestNameForDoc = onek + " " + guestNameForDoc;
                    }
                    // Open original pdf document
                    PdfDocument pdfDocument = getPdfDocument(pdfPath, getDestinationPath(outputPath, pdfPath, guestName), guestTc);
                    processDocument(pdfDocument, guestNameForDoc, guestTc, courseOwner, courseName, no, date);
                    pdfDocument.close();
                }
                System.out.println("Pdf processing for instruction row idx: "+ instructionRowIdx +" is completed.");
            }

        } catch (Exception e) {
            System.out.println("Error!: " + e.getMessage());
        }
    }

    private static boolean shouldSkipInstructionRow(String shouldProcess) {
        try {
            int bool = Integer.parseInt(shouldProcess);
            return bool != 1;
        } catch (Exception ignored) {}
        return true;
    }

    private static void processDocument(PdfDocument pdfDocument,
                                        String guestName,
                                        String guestTc,
                                        String courseOwner,
                                        String courseName,
                                        String no,
                                        String date) throws Exception {
        addHeaderAndFooter(pdfDocument, courseName, no, date);
        addWatermark(pdfDocument, guestName);
    }

    private static List<String[]> readGuestListCSV(String path) throws Exception {
        String nonNullPath;
        if (path != null) {
            nonNullPath = path;
        } else {
            System.out.println("Guest list path is null!");
            throw new Exception("Guest list path is null!");
        }
        return readCSV(nonNullPath, '\t', "UTF-16");
    }

    private static List<String[]> readTemplateInstructionsCSV() throws Exception {
        return readCSV(TEMPLATE_INSTRUCTIONS_PATH, ';', "UTF-8");
    }

    private static List<String[]> readCSV(String csvPath, Character separator, String fileCharset) throws Exception {
        final Reader reader = new InputStreamReader(new FileInputStream(csvPath), fileCharset);

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(separator)
                .build();

        CSVReader cr = new CSVReaderBuilder(reader)
                .withSkipLines(0)
                .withCSVParser(parser)
                .build();

        return cr.readAll();
    }

    private static PdfDocument getPdfDocument(String sourcePath, String destinationPath, String guestTc) throws Exception {
        log("File: " + new File(destinationPath).getName() + " Psswd: " + guestTc + " isPsswdEnabled: " + IS_PASSWORD_ENCRYPTION_ENABLED);

        WriterProperties wp = new WriterProperties();
        if (IS_PASSWORD_ENCRYPTION_ENABLED) {
            wp =  new WriterProperties()
                    .setStandardEncryption(
                            guestTc.getBytes(),
                            OWNER_PASSWORD.getBytes(),
                            0,
                            EncryptionConstants.ENCRYPTION_AES_256);
        }

        return new PdfDocument(new PdfReader(sourcePath),
                new PdfWriter(destinationPath, wp)
        );
    }

    private static void addHeaderAndFooter(PdfDocument pdfDoc,
                                           String courseName,
                                           String no,
                                           String date) throws Exception {
        Document doc = new Document(pdfDoc);

        String allHeader = date+" "+courseName+" "+no;
        int sizeDecrement = (allHeader.length() - 60) / 10;
        if (sizeDecrement < 0) {
            sizeDecrement = 0;
        }

        Paragraph headerAll = new Paragraph(allHeader)
                .setFont(PdfFontFactory.createFont(PDF_FONT))
                .setFontSize(HEADER_FONT_SIZE - sizeDecrement);



        Paragraph footer;

        for (int i = 3; i <= pdfDoc.getNumberOfPages() - 1; i++) {
            Rectangle pageSize = pdfDoc.getPage(i).getPageSize();
            float positionX;
            float positionY;

            // show header all
            positionX = pageSize.getWidth() / 2;
            positionY = pageSize.getTop() - 40;
            doc.showTextAligned(headerAll, positionX, positionY, i, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);

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

    private static String getDestinationPath(String outputPath, String pdfPath, String guestName) throws Exception {
        String camelCase = getCamelCaseFormat(guestName);
        File file = new File(outputPath);
        if (!file.exists()) {
            boolean isCreated = file.mkdir();
            if (!isCreated) {
                throw new Exception("Directory cannot be created!");
            }
        }
        File pdfFile = new File(pdfPath);
        if (!file.exists()) {
            throw new Exception("PDF file not found. Path: " + pdfPath);
        }

        String filenameWithExtension = pdfFile.getName();
        String filename = filenameWithExtension.substring(0, filenameWithExtension.indexOf("."));
        String output = outputPath + PATH_SEPERATOR_CURRENT + filename + "_" + camelCase + ".pdf";
        System.out.println("outputPath: "+ output);
        return output;
    }

    private static String getCamelCaseFormat(String name) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name.replace(" ", "_"));
    }
}