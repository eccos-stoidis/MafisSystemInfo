package com.ep.sysinfo.MafisSyStemInfo.service;

import com.ep.sysinfo.MafisSyStemInfo.model.Anlage;

import java.util.List;

public interface AnlageService {

    List<Anlage> findAllAnlagen();



    List<Anlage> findAnlagenByBetrieb(String betriebName);

    List<Anlage> findAnlagenByProfitCenter(String profitcenter);

    List<Anlage> findAnlagenByModulTyp(String modulTyp);

    List<Anlage> findAnlagenByKasseTypes(List<String> kasseTypes);

    List<Anlage> findAnlagenByTyp(String typ);

    List<Anlage> findAnlagenByUntertyp(String untertyp);

    List<Anlage> findAnlagenByTypAndUntertyp(String typ, String untertyp);

    List<Anlage> findAnlageByAutomaten(String engineVersion, String fccVersion, String typ, String unterTyp);

    List<Anlage> findAnlageByZutritts(String vonSektor, String nachSektor);

    List<Anlage> findAnlageByFiskaldatenTyp(String typSelection);

    List<Anlage> findAnlageByMedienarten(List<Integer> typ);
}
