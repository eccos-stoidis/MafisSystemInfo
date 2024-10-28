package com.ep.sysinfo.MafisSyStemInfo.repository;

import com.ep.sysinfo.MafisSyStemInfo.model.Fiskaldaten;
import com.ep.sysinfo.MafisSyStemInfo.model.SystemInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RepositoryRestResource
public interface FiskalDatenRepository extends JpaRepository<Fiskaldaten, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Fiskaldaten f WHERE f.system.system_id = :system_id")
    void deleteBySystemId(@Param("system_id") Long  system_id);



    @Transactional(readOnly=true)
    @Query("SELECT f FROM Fiskaldaten f WHERE f.system.system_id = :system_id")
    List<Fiskaldaten> getFiskalDatenBySystemId(@Param("system_id") Long  system_id);

    List<Fiskaldaten> findBySystem(SystemInfo system);
}
