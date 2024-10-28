package com.ep.sysinfo.MafisSyStemInfo.service;

import com.ep.sysinfo.MafisSyStemInfo.model.Fiskaldaten;
import com.ep.sysinfo.MafisSyStemInfo.model.FiskalDto;
import com.ep.sysinfo.MafisSyStemInfo.model.FiskalReg;

import java.util.List;

public interface FIskalService {
    List<FiskalReg> findAllFiskalReg();

    List<Fiskaldaten> findAllFiskalDaten();

    List<FiskalDto> listeFiskal();
}
