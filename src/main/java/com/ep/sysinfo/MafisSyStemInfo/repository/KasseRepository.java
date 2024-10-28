package com.ep.sysinfo.MafisSyStemInfo.repository;

import com.ep.sysinfo.MafisSyStemInfo.model.Kasse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource
public interface KasseRepository extends JpaRepository<Kasse, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Kasse k  WHERE k.system.system_id = :system_id ")
    void deleteBySystemId(@Param("system_id") Long  system_id);
}
