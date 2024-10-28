package com.ep.sysinfo.MafisSyStemInfo.service;

import com.ep.sysinfo.MafisSyStemInfo.model.Anlage;
import com.ep.sysinfo.MafisSyStemInfo.repository.AnlageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class AnlageServiceImpl implements AnlageService{

    private final AnlageRepository anlageRepository;
    @Override
    public List<Anlage> findAllAnlagen() {
        return anlageRepository.findAll();
    }
}
