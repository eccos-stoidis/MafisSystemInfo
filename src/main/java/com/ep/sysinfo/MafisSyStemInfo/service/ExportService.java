package com.ep.sysinfo.MafisSyStemInfo.service;

import com.ep.sysinfo.MafisSyStemInfo.model.Anlage;
import com.ep.sysinfo.MafisSyStemInfo.model.Computer;
import com.ep.sysinfo.MafisSyStemInfo.model.Fiskaldaten;
import com.ep.sysinfo.MafisSyStemInfo.model.Kundeninformation;
import com.ep.sysinfo.MafisSyStemInfo.repository.AnlageRepository;
import com.ep.sysinfo.MafisSyStemInfo.repository.ComputerRepository;
import com.ep.sysinfo.MafisSyStemInfo.repository.FiskalDatenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ExportService {

    @Autowired
    private AnlageRepository anlageRepository;

    @Autowired
    private ComputerRepository computerRepository;

    @Autowired
    private FiskalDatenRepository fiskalDatenRepository;

    public List<Kundeninformation> getKundenInformationen() {
        List<Kundeninformation> kundeninformationList = new ArrayList<>();

        List<Anlage> anlagen = anlageRepository.findAll();

        for (Anlage anlage : anlagen) {
            Kundeninformation kundenInformation = new Kundeninformation();

            // Populate fields from Anlage
            kundenInformation.setAnlagenNr(anlage.getAnlagenNr());
            kundenInformation.setAnlagenName(anlage.getAnlagenName());
            kundenInformation.setAnlagenOrt(anlage.getOrt());
            kundenInformation.setZuletzteAktualisiert(anlage.getSystem().getLastModified());
            kundenInformation.setIsAktiv(anlage.isStatus());
            kundenInformation.setMafisVersion(anlage.getMafisVersion());
            kundenInformation.setIsMafisTestbetrieb(anlage.getTestBetrieb());
            // Check if system updates are not null
            if (anlage.getSystem().getUpdates() != null) {
                kundenInformation.setIsMafisUpdateAktiv(anlage.getSystem().getUpdates().isAktiv());
                kundenInformation.setIsMafisAutoUpdate(anlage.getSystem().getUpdates().isAutoUpdate());
            } else {
                // Set default values if Updates is null
                kundenInformation.setIsMafisUpdateAktiv(false);  // or leave it null if that’s acceptable
                kundenInformation.setIsMafisAutoUpdate(false);   // or leave it null if that’s acceptable
            }

            // Fetch the corresponding Computer entity
            Computer computer = computerRepository.findByAnlagenNr(anlage.getAnlagenNr());
            if (computer != null) {
                kundenInformation.setServerComputerName(computer.getComputerName());
                kundenInformation.setServerIPAdresse(computer.getHostname());
                kundenInformation.setServerBetriebssystem(computer.getBetriebsSystem());
                kundenInformation.setServerBetriebssystemVersion(computer.getBetriebssystemVersion());
                kundenInformation.setServerJavaVersion(computer.getJavaVersion());
                kundenInformation.setServerJavaHome(computer.getJavaHome());
            }
            // Find all Fiskaldaten for the given system
            List<Fiskaldaten> fiskalDatenList = fiskalDatenRepository.findBySystem(anlage.getSystem());

            // Find the latest Fiskaldaten based on aktivAb property
            Fiskaldaten latestFiskalDaten = fiskalDatenList.stream()
                    .max(Comparator.comparing(Fiskaldaten::getAktivAb))
                    .orElse(null);

            // Populate KundenInformation if the latest Fiskaldaten is found and aktivAb is not null
            if (latestFiskalDaten != null && latestFiskalDaten.getAktivAb() != null) {
                kundenInformation.setFiskalTyp(latestFiskalDaten.getTyp());
                kundenInformation.setFiskalAktivAb(latestFiskalDaten.getAktivAb());
            }
            kundeninformationList.add(kundenInformation);
        }
        return kundeninformationList;
    }
}

