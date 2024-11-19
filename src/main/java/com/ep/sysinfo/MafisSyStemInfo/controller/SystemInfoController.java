package com.ep.sysinfo.MafisSyStemInfo.controller;

import com.ep.sysinfo.MafisSyStemInfo.model.*;
import com.ep.sysinfo.MafisSyStemInfo.repository.AutomatRepository;
import com.ep.sysinfo.MafisSyStemInfo.repository.SystemRepository;
import com.ep.sysinfo.MafisSyStemInfo.repository.ZutrittRepository;
import com.ep.sysinfo.MafisSyStemInfo.service.FIskalService;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.Image;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;




import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Controller to manage all system-related information
 */
@Controller
public class SystemInfoController extends TabellenController {

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private AutomatRepository automatRepository;

    @Autowired
    private ZutrittRepository zutrittRepository;

    @Autowired
    private FIskalService fiskalService;

    private static final Logger logger = LoggerFactory.getLogger(SystemInfoController.class);

    private Sort.Direction sortierReihenfolge = Sort.Direction.DESC;

    @Value("${datei.pfad}")
    private String dateiPfad;

    // Method to get the directory (optional, but useful)
    // Method to set the directory
    @Getter
    @Setter
    private String directory;

    /**
     * Fetches a list of all system information with optional search.js and pagination.
     *
     * @param model
     * @param seite
     * @param suchBegriff
     * @param sortierenNach
     * @param sortierReihenfolge
     * @param anzahlProSeite
     * @return
     */
    @GetMapping("/alleInformationen")
    public String liste(Model model,
                        @RequestParam(required = false) Optional<Integer> seite,
                        @RequestParam(required = false) Optional<String> suchBegriff,
                        @RequestParam(required = false) Optional<String> sortierenNach,
                        @RequestParam(required = false) Optional<String> sortierReihenfolge,
                        @RequestParam(required = false) Optional<String> anzahlProSeite) {
        try {
            Pageable paging = getPageable(seite.orElse(1), sortierReihenfolge.orElse("DESC"), sortierenNach.orElse("anlage.anlagenName"), anzahlProSeite.orElse("30"));

            Page<SystemInfo> systemInfoPage = suchBegriff
                    .filter(s -> !s.isEmpty())
                    .map(s -> systemRepository.sucheSystems(s, paging))
                    .orElseGet(() -> systemRepository.findAll(paging));

            List<SystemInfo> infos = systemInfoPage.getContent();
            model = aktualisiereModel(model, seite.orElse(1),
                    sortierReihenfolge.orElse("DESC"),
                    anzahlProSeite.orElse("30"),
                    sortierenNach.orElse(""),
                    suchBegriff.orElse(""),
                    systemInfoPage.getTotalPages(),
                    systemInfoPage.getTotalElements());
            model.addAttribute("infos", infos);
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der Systeminformationen: {}", e.getMessage(), e);
            model.addAttribute("ERROR", e.getMessage());
        }
        return "alleInformationen";
    }

    /**
     * Fetches information for a specific system based on the system ID.
     *
     * @param model
     * @param anlageId
     * @return
     */
    @GetMapping("/getSystemInfo/{anlageId}")
    public String getSystem(Model model, @PathVariable Long anlageId) {
        // Retrieve the SystemInfo using the repository and throw exception if not found
        SystemInfo info = Optional.ofNullable(systemRepository.findByAnlageAnlagenId(anlageId))
                .orElseThrow(() -> new ResourceNotFoundException("SystemInfo mit AnlageId " + anlageId + " wurde nicht gefunden."));

        // Fetch the list of AutomatenTypDTO using the system ID and throw exception if empty
        List<AutomatenTypDTO> automaten = automatRepository.holeTypen(info.getSystem_id());
        if (automaten.isEmpty()) {
            throw new ResourceNotFoundException("Keine AutomatenTypen für System ID " + info.getSystem_id() + " gefunden.");
        }

        // Set Automaten list in SystemInfo
        info.setAutomatenListe(automaten);

        // Add the info object to the model and return the view
        model.addAttribute("info", info);
        return "systemInfo";
    }

    /**
     * Fetches system information by the system's AnlagenNr.
     *
     * @param model
     * @param anlageNr
     * @return
     */
    @GetMapping("/getSystemInfoByAnlage/{anlageNr}")
    public String getSystemByAnlagenNr(Model model, @PathVariable Long anlageNr) {
        // Retrieve the SystemInfo using the repository and throw exception if not found
        SystemInfo info = Optional.ofNullable(systemRepository.findByAnlageAnlagenNr(anlageNr))
                .orElseThrow(() -> new ResourceNotFoundException("SystemInfo mit AnlagenNr " + anlageNr + " wurde nicht gefunden."));

        // Fetch the list of AutomatenTypDTO using the system ID
        List<AutomatenTypDTO> automaten = automatRepository.holeTypen(info.getSystem_id());
        if (automaten.isEmpty()) {
            throw new ResourceNotFoundException("Keine AutomatenTypen für System ID " + info.getSystem_id() + " gefunden.");
        }
        // Set Automaten list in SystemInfo
        info.setAutomatenListe(automaten);
        // Add the info object to the model and return the view
        model.addAttribute("info", info);
        return "systemInfo";
    }

    /**
     * Search for systems based on a query.
     *
     * @param q
     * @param model
     * @return
     */
    @GetMapping("/sucheSystem")
    public String search(@RequestParam(value = "search.js", required = false) String q, Model model) {
        List<SystemInfo> infos;

        if (q != null && !q.trim().isEmpty()) {
            // Parse the input and perform the search
            infos = (List<SystemInfo>) systemRepository.suchSystems(Long.parseLong(q.trim()), q.trim());
        } else {
            // Fetch all records with sorting
            Sort sort = Sort.by(Sort.Direction.ASC, "anlage.anlagenName");
            infos = systemRepository.findAll(sort);
        }

        model.addAttribute("infos", infos);
        return "alleInformationen";
    }

    /**
     * Sorts the system information by the provided field.
     *
     * @param model
     * @param sortBy
     * @return
     */
    @GetMapping("/sortSystem/{sortBy}")
    public String sortSystem(Model model, @PathVariable String sortBy) {
        // Toggle sort direction
        sortierReihenfolge = sortierReihenfolge == Sort.Direction.DESC ? Sort.Direction.ASC : Sort.Direction.DESC;
        // Create sort object and fetch sorted data
        Sort sort = Sort.by(sortierReihenfolge, sortBy);
        List<SystemInfo> infos = systemRepository.findAll(sort);
        // Add data to model
        model.addAttribute("infos", infos);
        return "alleInformationen";
    }

    /**
     * Exports CSV data for a specific system.
     *
     * @param anlageNr
     * @param model
     * @return
     */
    @GetMapping("/exportCVSDaten/{anlageNr}")
    public ResponseEntity<InputStreamResource> erstelleCSV(@PathVariable Long anlageNr, Model model) throws IOException {
        // Retrieve the SystemInfo
        SystemInfo info = Optional.ofNullable(systemRepository.findByAnlageAnlagenNr(anlageNr))
                .orElseThrow(() -> new ResourceNotFoundException("SystemInfo mit AnlagenNr " + anlageNr + " wurde nicht gefunden."));

        // Generate CSV file
        String fileName = "Fiskal_" + sanitizeFileName(info.getAnlage().getAnlagenName()) + ".csv";
        File csvFile = createCSVFile(info, fileName);

        // Return the file as a downloadable response
        return downloadDatei(fileName, csvFile);
    }

    private File createCSVFile(SystemInfo info, String fileName) throws IOException {
        Path path = Paths.get(dateiPfad);
        File file = new File(path.toString() + "/" + fileName);
        try (PrintWriter writer = new PrintWriter(file)) {
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Bezeichnung", "SystemId", "Typ", "aktiv ab", "Format"));
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            for (Fiskaldaten item : info.getFiskalService()) {
                csvPrinter.printRecord(
                        item.getBezeichnung(),
                        item.getFiskalSystemId(),
                        item.getTyp(),
                        format.format(java.sql.Timestamp.valueOf(item.getAktivAb())),
                        item.getFormat()
                );
            }
            csvPrinter.flush();
        }
        return file;
    }

    private String sanitizeFilePath(String fileName) {
        return fileName.trim().replaceAll("[^A-Za-z0-9]", "_");
    }

    /**
     * Helper method to download a file.
     *
     * @param fileName
     * @param file
     * @return
     * @throws IOException
     */
    private ResponseEntity<InputStreamResource> downloadDatei(String fileName, File file) throws IOException {
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }

    /**
     * Fetches all Fiskal entries and adds them to the model.
     */
    @GetMapping("/alleFiskal")
    public String alleFiskal(Model model) {
        // Retrieve all fiskal records and apply manual pagination
        List<FiskalDto> allFiskal = fiskalService.listeFiskal();
        if (allFiskal.isEmpty()) {
            throw new ResourceNotFoundException("Keine Fiskal-Daten gefunden.");
        }
        model.addAttribute("fiskal", allFiskal);
        return "alleFiskal";
    }


    /**
     * Exports PDF data for a specific system.
     *
     * @param anlageNr
     * @param model
     * @return
     */
    @GetMapping("/exportPDFDaten/{anlageNr}")
    public ResponseEntity<InputStreamResource> erstellePdf(@PathVariable Long anlageNr, Model model) throws IOException, DocumentException {
        SimpleDateFormat formatTag = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat formatVoll = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        // Retrieve SystemInfo or throw an exception if not found
        SystemInfo info = Optional.ofNullable(systemRepository.findByAnlageAnlagenNr(anlageNr))
                .orElseThrow(() -> new ResourceNotFoundException("SystemInfo mit AnlagenNr " + anlageNr + " wurde nicht gefunden."));

        String anlagenName = sanitizeFileName(info.getAnlage().getAnlagenName());
        String filename = "fiskal_" + anlagenName + ".pdf";

        Path path = Paths.get(dateiPfad);
        setDirectory(path.toString());

        File pdfFile = new File(getDirectory() + "/" + filename);
        pdfFile.getParentFile().mkdirs();

        Document document = new Document(PageSize.A4.rotate(), 35f, 35f, 35f, 25f);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        document.open();
        document.addTitle("Fiskaldaten");
        document.addSubject("Mafis InfoSystem");
        document.addKeywords("Java, PDF, iText");
        document.addCreator("eccos pro gmbH");


        Image image = Image.getInstance(Objects.requireNonNull(getClass().getResource("/static/images/logo.png")));
        document.add(image);


        Collection<Fiskaldaten> fiskalDaten = info.getFiskalService();

        Font catFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph subPara = new Paragraph("Fiskaldaten für: " + info.getAnlage().getAnlagenName() + " Anlagen-Nr.:" + info.getAnlage().getAnlagenNr(), catFont);
        Phrase datumText = new Phrase("Download am: " + formatVoll.format(new Date()));

        document.add(subPara);
        document.add(datumText);
        document.add(Chunk.NEWLINE);

        addFiskalDataToPdf(document, fiskalDaten, formatTag);

        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT,
                    new Phrase("@eccos pro gmbh. Alle Rechte vorbehalten.", new Font(Font.FontFamily.HELVETICA, 9)),
                    document.leftMargin(), document.bottom() - 10, 0);

        document.close();

        return downloadDatei(filename, pdfFile);

    }

    private void addFiskalDataToPdf(Document document, Collection<Fiskaldaten> fiskalDaten, SimpleDateFormat formatTag) throws DocumentException {
        Font font11 = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
        Font font10 = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        if (fiskalDaten != null && !fiskalDaten.isEmpty()) {
            for (Fiskaldaten item : fiskalDaten) {
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setSpacingAfter(3);
                float[] columnWidths = {20f, 32f, 12f, 10f, 9f};
                table.setWidths(columnWidths);

                addTableHeader(table, font11);
                addTableContent(table, item, formatTag, font11);
                document.add(table);

                if (item.getRegListe() != null && !item.getRegListe().isEmpty()) {
                    PdfPTable tableReg = new PdfPTable(6);
                    tableReg.setWidthPercentage(100);
                    float[] columnWidthsR = {13f, 27f, 11f, 22f, 7f, 8f};
                    tableReg.setWidths(columnWidthsR);

                    addTableRegHeader(tableReg, font10);
                    addTableRegContent(tableReg, item.getRegListe(), font10);
                    document.add(tableReg);
                    document.add(Chunk.NEWLINE);
                }
            }
        } else {
            document.add(new Phrase("Keine Daten vorhanden!"));
        }
    }

    private void addTableHeader(PdfPTable table, Font font) {
        String[] headers = {"Bezeichnung", "System-ID", "Typ", "aktiv ab", "Format"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }
    }

    private void addTableContent(PdfPTable table, Fiskaldaten item, SimpleDateFormat formatTag, Font font) {
        table.addCell(new PdfPCell(new Phrase(item.getBezeichnung(), font)));
        table.addCell(new PdfPCell(new Phrase(item.getFiskalSystemId(), font)));
        table.addCell(new PdfPCell(new Phrase(item.getTyp(), font)));
        table.addCell(new PdfPCell(new Phrase(item.getAktivAb() != null ? formatTag.format(java.sql.Timestamp.valueOf(item.getAktivAb())) : "N/A", font)));
        table.addCell(new PdfPCell(new Phrase(item.getFormat().equalsIgnoreCase("N") ? "normal" : item.getFormat(), font)));
    }

    private void addTableRegHeader(PdfPTable table, Font font) {
        String[] headers = {"Bezeichnung", "Client/Cashbox-ID", "Reg/Term-ID", "Endpoint", "Benutzt", "Registriert"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }
    }

    private void addTableRegContent(PdfPTable table, List<FiskalReg> regListe, Font font) {
        for (FiskalReg reg : regListe) {
            table.addCell(new PdfPCell(new Phrase(reg.getBezeichnung(), font)));
            table.addCell(new PdfPCell(new Phrase(reg.getKennung(), font)));
            table.addCell(new PdfPCell(new Phrase(reg.getRegisterNr(), font)));
            table.addCell(new PdfPCell(new Phrase(reg.getEndPoint(), font)));
            table.addCell(new PdfPCell(new Phrase(reg.isBenutzt() ? "Ja" : "Nein", font)));
            table.addCell(new PdfPCell(new Phrase(reg.isRegistriert() ? "Ja" : "Nein", font)));
        }
    }

    @RequestMapping(value = "/exportFiskalCVSDaten", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> erstelleFiskalCSV(Model model) {
        try {
            // Fetch the list of FiskalDto objects
            List<FiskalDto> fiskalList = fiskalService.listeFiskal();

            // Name of the CSV file
            String fileName = "FiskalReport.csv";

            // Get the path from configuration and ensure it's set
            Path path = Paths.get(dateiPfad);
            setDirectory(path.toString());

            // Define the file path where the CSV will be saved
            File file = new File(getDirectory() + "/" + fileName);

            // Ensure the parent directory exists, create it if it doesn't
            File parentDirectory = file.getParentFile();
            if (parentDirectory != null && !parentDirectory.exists()) {
                boolean isCreated = parentDirectory.mkdirs();  // Create the directory if it doesn't exist
                if (!isCreated) {
                    // Handle the case where the directory could not be created
                    throw new IOException("Failed to create the directory: " + parentDirectory.getAbsolutePath());
                }
            }

            // Write the CSV file
            try (PrintWriter writer = new PrintWriter(file)) {
                // Use CSVFormat.DEFAULT to avoid deprecated warnings
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                        "AnlagenNr", "AnlagenName", "Mafis-Version", "Typ", "CashboxAnzahl", "istRegistriert"
                ));

                // Iterate through the list of FiskalDto objects and write each one to the CSV
                for (FiskalDto item : fiskalList) {
                    csvPrinter.printRecord(
                            item.getAnlagenNr(),
                            item.getAnlagenName(),
                            item.getMafisVersion(),
                            item.getTyp(),
                            item.getFiskalCount(),  // Corrected method
                            item.getRegistriert() ? "Ja" : "Nein"
                    );
                }
                csvPrinter.flush();
            }

            // Return the file for download
            return downloadDatei(fileName, file);

        } catch (Exception e) {
            // Handle any errors during file creation and logging
            logger.error("Fehler beim Erstellen der Fiskal-CSV: {}", e.getMessage(), e);
            model.addAttribute("ERROR", e.getMessage());
            return ResponseEntity.status(500).build();  // Return an error response in case of failure
        }
    }



    private String sanitizeFileName(String fileName) {
        return fileName.trim().replaceAll("[^A-Za-z0-9]", "_");
    }


}
