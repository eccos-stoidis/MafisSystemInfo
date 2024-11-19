package com.ep.sysinfo.MafisSyStemInfo.repository;

import com.ep.sysinfo.MafisSyStemInfo.model.Betreiber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BetreiberRepository  extends JpaRepository<Betreiber, Long> {

    Optional<Betreiber> findByAnlagenNr(Long anlagenNr);
}
