package com.ep.sysinfo.MafisSyStemInfo.repository;

import com.ep.sysinfo.MafisSyStemInfo.model.Automat;
import com.ep.sysinfo.MafisSyStemInfo.model.AutomatenTypDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RepositoryRestResource
public interface AutomatRepository extends JpaRepository<Automat, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Automat a  WHERE a.system.system_id = :system_id ")
    void deleteBySystemId(@Param("system_id") Long  system_id);

    @Transactional(readOnly=true)
    @Query("SELECT new com.ep.sysinfo.MafisSyStemInfo.model.AutomatenTypDTO (typ, COUNT(typ)) FROM Automat WHERE system.system_id = :system_id  GROUP BY typ ")
    List<AutomatenTypDTO> holeTypen(@Param("system_id") Long  system_id);



}
