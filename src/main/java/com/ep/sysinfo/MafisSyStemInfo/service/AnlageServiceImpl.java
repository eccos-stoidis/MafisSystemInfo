package com.ep.sysinfo.MafisSyStemInfo.service;

import com.ep.sysinfo.MafisSyStemInfo.model.Anlage;
import com.ep.sysinfo.MafisSyStemInfo.repository.AnlageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AnlageServiceImpl implements AnlageService{

    private final AnlageRepository anlageRepository;
    @Override
    public List<Anlage> findAllAnlagen() {
        return anlageRepository.findAll();
    }

    public List<Anlage> findAnlagenByBetrieb(String betriebName) {
        return anlageRepository.findAnlagenByBetriebName(betriebName);
    }

    @Override
    public List<Anlage> findAnlagenByProfitCenter(String profitcenter) {
        return anlageRepository.findAnlageByZugProfitcenter(profitcenter);
    }

    @Override
    public List<Anlage> findAnlagenByModulTyp(String modulTyp) {
        return anlageRepository.findAnlageByModulTyp(modulTyp);
    }

    @Override
    public List<Anlage> findAnlagenByKasseTypes(List<String> kasseTypes) {
        return anlageRepository.findAnlagenByKasseTypes(kasseTypes);
    }

    @Override
    public List<Anlage> findAnlagenByTyp(String typ) {
        return anlageRepository.findByTyp(typ);
    }

    @Override
    public List<Anlage> findAnlagenByUntertyp(String untertyp) {
        return anlageRepository.findByUntertyp(untertyp);
    }

    @Override
    public List<Anlage> findAnlagenByTypAndUntertyp(String typ, String untertyp) {
        return anlageRepository.findByTypAndUntertyp(typ, untertyp);
    }

    @Override
    public List<Anlage> findAnlageByAutomaten(String engineVersion, String fccVersion, String typ, String unterTyp) {
        return anlageRepository.findAnlageByAutomatenParams(
                engineVersion != null && !engineVersion.isEmpty() ? engineVersion : null,
                fccVersion != null && !fccVersion.isEmpty() ? fccVersion : null,
                typ != null && !typ.isEmpty() ? typ : null,
                unterTyp != null && !unterTyp.isEmpty() ? unterTyp : null
        );
    }

    @Override
    public List<Anlage> findAnlageByZutritts(String vonSektor, String nachSektor) {
        return anlageRepository.findAnlageByZutrittsParams(
                vonSektor != null && !vonSektor.isEmpty() ? vonSektor : null,
                nachSektor != null && !nachSektor.isEmpty() ? nachSektor : null
        );
    }


    @Override
    public List<Anlage> findAnlageByFiskaldatenTyp(String typSelection) {
        if ("efsta".equalsIgnoreCase(typSelection)) {
            return anlageRepository.findAnlageByFiskaldatenEfsta();
        } else if ("fiskaltrust".equalsIgnoreCase(typSelection)) {
            return anlageRepository.findAnlageByFiskaldatenFiskaltrust();
        } else {
            return anlageRepository.findAll(); // Assuming all records when "all" is selected
        }
    }

    @Override
    public List<Anlage> findAnlageByMedienarten(List<Integer> typ) {
        return anlageRepository.findAnlageByMedienarten(typ);
    }


}
