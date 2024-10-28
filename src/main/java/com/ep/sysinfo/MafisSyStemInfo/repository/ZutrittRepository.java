package com.ep.sysinfo.MafisSyStemInfo.repository;

import com.ep.sysinfo.MafisSyStemInfo.model.AutomatenTypDTO;
import com.ep.sysinfo.MafisSyStemInfo.model.Zutritt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RepositoryRestResource
public interface ZutrittRepository extends JpaRepository<Zutritt, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Zutritt z  WHERE z.system.system_id = :system_id ")
    void deleteBySystemId(@Param("system_id") Long  system_id);

    @Transactional(readOnly=true)
    @Query("SELECT new com.ep.sysinfo.MafisSyStemInfo.model.AutomatenTypDTO (vonSektor, COUNT(vonSektor)) FROM Zutritt"
            + " WHERE system.system_id = :system_id  and vonSektor not like '%außerhalb%'   GROUP BY vonSektor ")
    List<Zutritt> holeTypen(@Param("system_id") Long  system_id);


}
