package com.ep.sysinfo.MafisSyStemInfo.repository;

import com.ep.sysinfo.MafisSyStemInfo.model.MedienArt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource
public interface MedienArtRepository extends JpaRepository<MedienArt, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM MedienArt m WHERE m.system.system_id = :system_id ")
    void deleteBySystemId(@Param("system_id") Long  system_id);
}
