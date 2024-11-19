package com.ep.sysinfo.MafisSyStemInfo.service;

import com.ep.sysinfo.MafisSyStemInfo.model.*;
import com.ep.sysinfo.MafisSyStemInfo.repository.AnlageRepository;
import com.ep.sysinfo.MafisSyStemInfo.repository.ComputerRepository;
import com.ep.sysinfo.MafisSyStemInfo.repository.FiskalDatenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExportService {

    @Autowired
    private AnlageRepository anlageRepository;

    @Autowired
    private ComputerRepository computerRepository;

    @Autowired
    private FiskalDatenRepository fiskalDatenRepository;

    /**
     * Ruft alle Kundeninformationen in mehreren Schritten ab:
     * Schritt 1: Abrufen aller aktiven Anlagen mit Systemen und Updates in einer einzigen Abfrage
     * Schritt 2: Abrufen aller Computer, die den Anlagen zugeordnet sind, in einem Batch
     * Schritt 3: Abrufen aller Fiskaldaten, die den Systemen zugeordnet sind, in einem Batch
     * Schritt 4: Zuordnung jeder Anlage zu einem Kundeninformations-Objekt
     *
     * @return          eine Liste von Kundeninformations-Objekten
     */

    public List<Kundeninformation> getKundenInformationen() {
        // Schritt 1: Abrufen aller aktiven Anlagen mit Systemen und Updates in einer einzigen Abfrage
        List<Anlage> anlagen = anlageRepository.findAllActiveAnlagen();

        // Schritt 2: Abrufen aller Computer, die den Anlagen zugeordnet sind, in einem Batch
        List<Long> anlagenNrs = anlagen.stream()
                .map(Anlage::getAnlagenNr)
                .toList();
        Map<Long, Computer> computerMap = computerRepository.findAllByAnlagenNrs(anlagenNrs).stream()
                .collect(Collectors.toMap(Computer::getAnlagenNr, Function.identity()));

        // Schritt 3: Abrufen aller Fiskaldaten, die den Systemen zugeordnet sind, in einem Batch
        List<SystemInfo> systems = anlagen.stream()
                .map(Anlage::getSystem)
                .toList();
        Map<SystemInfo, Fiskaldaten> fiskalDatenMap = fiskalDatenRepository.findAllBySystems(systems).stream()
                .collect(Collectors.groupingBy(Fiskaldaten::getSystem,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(Fiskaldaten::getAktivAb)),
                                optional -> optional.orElse(null))));


        // Schritt 4: Zuordnung jeder Anlage zu einem Kundeninformations-Objekt
        return anlagen.stream().map(anlage -> {
            SystemInfo system = anlage.getSystem();
            Updates updates = system != null ? system.getUpdates() : null;

            // Extrahiere Computer- und Fiskaldaten-Details
            Computer computer = computerMap.get(anlage.getAnlagenNr());
            Fiskaldaten latestFiskalDaten = fiskalDatenMap.get(system);

            // Erstelle und befülle das Kundeninformations-Objekt
            Kundeninformation kundenInformation = new Kundeninformation();
            kundenInformation.setAnlagenNr(anlage.getAnlagenNr());
            kundenInformation.setAnlagenName(anlage.getAnlagenName());
            kundenInformation.setAnlagenOrt(anlage.getOrt());
            kundenInformation.setZuletzteAktualisiert(system != null ? system.getLastModified() : null);
            kundenInformation.setIsAktiv(anlage.isStatus());
            kundenInformation.setMafisVersion(anlage.getMafisVersion());
            kundenInformation.setIsMafisTestbetrieb(anlage.getTestBetrieb());
            kundenInformation.setIsMafisUpdateAktiv(updates != null && updates.isAktiv());
            kundenInformation.setIsMafisAutoUpdate(updates != null && updates.isAutoUpdate());

            if (computer != null) {
                kundenInformation.setServerComputerName(computer.getComputerName());
                kundenInformation.setServerIPAdresse(computer.getHostname());
                kundenInformation.setServerBetriebssystem(computer.getBetriebsSystem());
                kundenInformation.setServerBetriebssystemVersion(computer.getBetriebssystemVersion());
                kundenInformation.setServerJavaVersion(computer.getJavaVersion());
                kundenInformation.setServerJavaHome(computer.getJavaHome());
            }

            if (latestFiskalDaten != null) {
                kundenInformation.setFiskalTyp(latestFiskalDaten.getTyp());
                kundenInformation.setFiskalAktivAb(latestFiskalDaten.getAktivAb());
            }

            return kundenInformation;
        }).toList();
    }
}

