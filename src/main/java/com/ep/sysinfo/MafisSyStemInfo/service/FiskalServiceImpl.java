package com.ep.sysinfo.MafisSyStemInfo.service;

import com.ep.sysinfo.MafisSyStemInfo.model.Anlage;
import com.ep.sysinfo.MafisSyStemInfo.model.Fiskaldaten;
import com.ep.sysinfo.MafisSyStemInfo.model.FiskalDto;
import com.ep.sysinfo.MafisSyStemInfo.model.FiskalReg;
import com.ep.sysinfo.MafisSyStemInfo.repository.FiskalDatenRepository;
import com.ep.sysinfo.MafisSyStemInfo.repository.FiskalRegRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FiskalServiceImpl implements FIskalService {

    private final FiskalRegRepository fiskalRegRepository;
    private final FiskalDatenRepository fiskalDatenRepository;

    @Autowired
    AnlageService anlageService;

    @Override
    public List<FiskalReg> findAllFiskalReg() {
        return fiskalRegRepository.findAll();
    }


    @Override
    public List<Fiskaldaten> findAllFiskalDaten() {
        return fiskalDatenRepository.findAll();
    }

    public List<FiskalDto> listeFiskal() {
        List<FiskalReg> fiskalRegs = findAllFiskalReg();
        List<Fiskaldaten> fiskalDaten = findAllFiskalDaten();
        List<Anlage> anlagen = anlageService.findAllAnlagen();

        List<FiskalDto> fiskalDtos = new ArrayList<>();

        // For each Anlage, find the matching Fiskaldaten and FiskalReg
        for (Anlage anlage : anlagen) {
            List<Fiskaldaten> relevantFiskaldaten = fiskalDaten.stream()
                    .filter(fd -> fd.getSystem().getSystem_id().equals(anlage.getSystem().getSystem_id()))
                    .toList();

            for (Fiskaldaten fiskaldaten : relevantFiskaldaten) {
                long fiskalCount = fiskalRegs.stream()
                        .filter(fr -> fr.getFiskal().getFiskalId().equals(fiskaldaten.getFiskalId()))
                        .count();

                boolean isRegistered = fiskalRegs.stream()
                        .anyMatch(fr -> fr.getFiskal().getFiskalId().equals(fiskaldaten.getFiskalId()) && fr.isRegistriert());

                FiskalDto dto = new FiskalDto(
                        anlage.getAnlagenNr(),
                        anlage.getAnlagenName(),
                        anlage.getMafisVersion(),
                        fiskaldaten.getTyp(),
                        fiskalCount,
                        isRegistered
                );

                fiskalDtos.add(dto);
            }
        }
        // Sort by AnlagenName
        fiskalDtos.sort((dto1, dto2) -> dto1.getAnlagenName().compareTo(dto2.getAnlagenName()));

        return fiskalDtos;
    }


}
