package com.ep.sysinfo.MafisSyStemInfo.service;

import com.ep.sysinfo.MafisSyStemInfo.model.Fiskaldaten;
import com.ep.sysinfo.MafisSyStemInfo.model.FiskalDto;
import com.ep.sysinfo.MafisSyStemInfo.model.FiskalReg;

import java.util.List;

public interface FIskalService {
    /**
     * Ruft alle Fiskal-Registrierungseinträge ab.
     *
     * @return         eine Liste von FiskalReg-Objekten, die die Fiskal-Registrierungseinträge repräsentieren
     */
    List<FiskalReg> findAllFiskalReg();

    /**
     * Ruft alle Fiskal-Daten ab.
     *
     * @return         eine Liste von FiskalDaten-Objekten, die die Fiskal-Daten repräsentieren
     */
    List<Fiskaldaten> findAllFiskalDaten();

    /**
     * Erstellt eine Liste von FiskalDto-Objekten.
     *
     * @return         eine Liste von FiskalDto-Objekten
     */
    List<FiskalDto> listeFiskal();
}
