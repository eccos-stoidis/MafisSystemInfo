package com.ep.sysinfo.MafisSyStemInfo.controller;

import com.ep.sysinfo.MafisSyStemInfo.model.Kundeninformation;
import com.ep.sysinfo.MafisSyStemInfo.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class ExportController {

    @Autowired
    private ExportService exportService;

    @GetMapping("/exportKundeninformationen")
    public ResponseEntity<Resource> exportAlleKundeninformationenAlsCsv() {
        List<Kundeninformation> kundenInformationen = exportService.getKundenInformationen();
        StringBuilder csv = erstelleCsvInhalt(kundenInformationen);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy");
        String heutigesDatum = LocalDateTime.now().format(formatter);
        String filename = heutigesDatum + "_kundeninformationen_export.csv";
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(csv.toString().getBytes()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }

    private static StringBuilder erstelleCsvInhalt(List<Kundeninformation> kundenInformationen) {
        StringBuilder csv = new StringBuilder();
        String csvHeader = erstelleCsvHeader();
        csv.append(csvHeader).append("\n");
        kundenInformationen.forEach(kundeninformation -> csv.append(kundeninformation.exportAlsCsv()).append("\n"));
        return csv;
    }

    private static String erstelleCsvHeader() {
        List<String> csvHead = new ArrayList<String>();
        csvHead.add("Anlagen Nummer");
        csvHead.add("Anlagen Name");
        csvHead.add("Anlagen Ort");
        csvHead.add("zuletzt aktualisiert");
        csvHead.add("STATUS (aktiv/deaktiviert)");
        csvHead.add("mafis - Version");
        csvHead.add("mafis - Testbetrieb");
        csvHead.add("mafis - Updates-Aktiv");
        csvHead.add("mafis - Autom. Updates");
        csvHead.add("Server - Computer Name");
        csvHead.add("Server - IP Adresse");
        csvHead.add("Server - Betriebssystem");
        csvHead.add("Server - OS-Version");
        csvHead.add("Server - Java-Version");
        csvHead.add("Server - Java Home");
        csvHead.add("Fiskal - Typ");
        csvHead.add("Fiskal - aktiv ab");
        return String.join(";", csvHead);
    }
}
