package com.ep.sysinfo.MafisSyStemInfo.service;

import com.ep.sysinfo.MafisSyStemInfo.model.Anlage;
import com.ep.sysinfo.MafisSyStemInfo.model.Betreiber;
import com.ep.sysinfo.MafisSyStemInfo.repository.AnlageRepository;
import com.ep.sysinfo.MafisSyStemInfo.repository.BetreiberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class AnlageServiceImpl implements AnlageService{

    private final AnlageRepository anlageRepository;

    private final BetreiberRepository betreiberRepository;
    @Override
    public List<Anlage> findAllAnlagen() {
        return anlageRepository.findAll();
    }

    public List<Anlage> findAnlagenByBetrieb(String betriebName) {
        // Fetch all Anlagen using the repository method with @EntityGraph
        List<Anlage> anlagen = anlageRepository.findAllActiveAnlagen();

        // Filter and sort the Anlagen by the Betrieb name
        return anlagen.stream()
                .filter(anlage -> anlage.getSystem()
                        .getBetriebe()
                        .stream()
                        .anyMatch(betrieb -> betrieb.getBetriebName().equals(betriebName)))
                .sorted(Comparator.comparing(Anlage::getAnlagenName, Comparator.nullsLast(String::compareTo))) // Sort by name, handling nulls
                .toList(); // Collect as a list
    }

    @Override
    public List<Anlage> findAnlagenByProfitCenter(String profitcenter) {
        List<Anlage> anlagen = anlageRepository.findAllActiveAnlagen();

        // Filter and sort the Anlagen by profitCenter name
        return anlagen.stream()
                .filter(anlage -> anlage.getSystem()
                        .getProfitCenter()
                        .stream()
                        .anyMatch(pc -> pc.getZugProfitcenter().equals(profitcenter))) // Filter by profitcenter
                .sorted(Comparator.comparing(Anlage::getAnlagenName, Comparator.nullsLast(String::compareTo))) // Sort by name
                .toList(); // Collect as a list
    }

    @Override
    public List<Anlage> findAnlagenByModulTyp(String modulTyp) {
        List<Anlage> anlagen = anlageRepository.findAllActiveAnlagen();

        return anlagen.stream()
                .filter(anlage -> anlage.getSystem()
                        .getModule()
                        .stream()
                        .anyMatch(modul -> modul.getModulTyp().equals(modulTyp))) // Filter by modulTyp
                .sorted(Comparator.comparing(Anlage::getAnlagenName, Comparator.nullsLast(String::compareTo))) // Sort by name
                .toList(); // Collect as a list
    }

    @Override
    public List<Anlage> findAnlagenByKasseTypes(List<String> kasseTypes) {
        List<Anlage> anlagen = anlageRepository.findAllActiveAnlagen();
        return anlagen.stream()
                .filter(anlage -> anlage.getSystem()
                        .getKassen()
                        .stream()
                        .anyMatch(kasse -> kasseTypes.contains(kasse.getTyp()))) // Filter by kasse types
                .sorted(Comparator.comparing(Anlage::getAnlagenName, Comparator.nullsLast(String::compareTo))) // Sort by name
                .toList(); // Collect as a list
    }

    @Override
    public List<Anlage> findAnlageBySchnittstellen(String typ, String untertyp) {
        // Fetch all active Anlagen with required relationships
        List<Anlage> anlagen = anlageRepository.findAllActiveAnlagen();

        // Filter the Anlagen based on provided `typ` and `untertyp`
        return anlagen.stream()
                .filter(anlage -> anlage.getSystem().getSchnittstellen().stream().anyMatch(schnittstelle ->
                        (typ == null || typ.isEmpty() || typ.equals(schnittstelle.getTyp())) &&
                                (untertyp == null || untertyp.isEmpty() || untertyp.equals(schnittstelle.getUnterTyp()))
                )) // Apply filtering criteria for both `typ` and `untertyp`
                .sorted(Comparator.comparing(Anlage::getAnlagenName, Comparator.nullsLast(String::compareTo))) // Sort by name
                .toList(); // Collect as a list
    }

    @Override
    public List<Anlage> findAnlageByAutomaten(String engineVersion, String fccVersion, String typ, String unterTyp) {
        // Fetch all active Anlagen with required relationships
        List<Anlage> anlagen = anlageRepository.findAllActiveAnlagen();

        // Filter the Anlagen based on provided parameters
        return anlagen.stream()
                .filter(anlage -> anlage.getSystem().getAutomaten().stream().anyMatch(auto ->
                        (engineVersion == null || engineVersion.isEmpty() || engineVersion.equals(auto.getEngineVersion())) &&
                                (fccVersion == null || fccVersion.isEmpty() || fccVersion.equals(auto.getFccVersion())) &&
                                (typ == null || typ.isEmpty() || typ.equals(auto.getTyp())) &&
                                (unterTyp == null || unterTyp.isEmpty() || unterTyp.equals(auto.getUnterTyp()))
                )) // Apply all filtering criteria
                .sorted(Comparator.comparing(Anlage::getAnlagenName, Comparator.nullsLast(String::compareTo))) // Sort by name
                .toList(); // Collect as a list
    }

    @Override
    public List<Anlage> findAnlageByZutritts(String vonSektor, String nachSektor) {
        // Fetch all active Anlagen with required relationships
        List<Anlage> anlagen = anlageRepository.findAllActiveAnlagen();

        // Filter the Anlagen based on provided parameters
        return anlagen.stream()
                .filter(anlage -> anlage.getSystem().getZutritts().stream().anyMatch(zutritt ->
                        (vonSektor == null || vonSektor.isEmpty() || vonSektor.equals(zutritt.getVonSektor())) &&
                                (nachSektor == null || nachSektor.isEmpty() || nachSektor.equals(zutritt.getNachSektor()))
                )) // Apply all filtering criteria
                .sorted(Comparator.comparing(Anlage::getAnlagenName, Comparator.nullsLast(String::compareTo))) // Sort by name
                .toList(); // Collect as a list
    }


    @Override
    public List<Anlage> findAnlageByFiskaldatenTyp(String typSelection) {
        // Fetch all active Anlagen with required relationships
        List<Anlage> anlagen = anlageRepository.findAllActiveAnlagen();

        // Filter the Anlagen based on the provided `typSelection`
        return anlagen.stream()
                .filter(anlage -> anlage.getSystem().getFiskalService().stream().anyMatch(fiskalService ->
                        "efsta".equalsIgnoreCase(typSelection) && "efsta".equalsIgnoreCase(fiskalService.getTyp()) ||
                                "fiskaltrust".equalsIgnoreCase(typSelection) && "fiskaltrust".equalsIgnoreCase(fiskalService.getTyp()) ||
                                "all".equalsIgnoreCase(typSelection) // Include all if "all" is selected
                )) // Apply filtering criteria
                .sorted(Comparator.comparing(Anlage::getAnlagenName, Comparator.nullsLast(String::compareTo))) // Sort by name
                .toList(); // Collect as a list
    }

    @Override
    public List<Anlage> findAnlageByMedienarten(List<Integer> typ) {
        List<Anlage> anlagen = anlageRepository.findAllActiveAnlagen();
        return anlagen.stream()
                .filter(anlage -> anlage.getSystem().getMedienArten().stream().anyMatch(medienArt ->
                        (typ == null || typ.isEmpty() || typ.contains(medienArt.getTypNr()))
                )) // Apply filtering criteria
                .sorted(Comparator.comparing(Anlage::getAnlagenName, Comparator.nullsLast(String::compareTo))) // Sort by name
                .toList(); // Collect as a list
    }

    @Override
    public List<Anlage> findAnlageByBetreiber(String betreiberName) {
        List<Betreiber> betreiberList = betreiberRepository.findByBetreiberName(betreiberName);
        List<Long> anlagenNrList = betreiberList.stream()
                .map(Betreiber::getAnlagenNr)
                .toList();
        return anlageRepository.findByAnlagenNrIn(anlagenNrList);

    }


}
