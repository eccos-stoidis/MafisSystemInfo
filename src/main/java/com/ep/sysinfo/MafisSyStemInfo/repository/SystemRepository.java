package com.ep.sysinfo.MafisSyStemInfo.repository;

import com.ep.sysinfo.MafisSyStemInfo.model.SystemInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface SystemRepository extends JpaRepository<SystemInfo, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE SystemInfo s SET s.lastModified  =:datum WHERE s.system_id= :system_id  ")
    void updateSystem(@Param("system_id") Long  system_id, @Param("datum") LocalDateTime datum);

    SystemInfo findByAnlageAnlagenNr(Long anlageNr);

    @Transactional(readOnly = true)
    @Query("SELECT a FROM SystemInfo a WHERE a.anlage.status = true AND (CAST(a.anlage.anlagenNr AS string) LIKE CONCAT('%', :suchbegriff, '%') OR a.anlage.anlagenName LIKE CONCAT('%', :suchbegriff, '%')) ORDER BY a.anlage.anlagenName")
    Page<SystemInfo> sucheSystems(@Param("suchbegriff") String suchbegriff, Pageable pageable);


    @Transactional(readOnly = true)
    @Query("SELECT a FROM SystemInfo a WHERE a.anlage.status = true AND (a.anlage.anlagenNr = :suchId OR a.anlage.anlagenName LIKE CONCAT('%', :anlagenName, '%')) ORDER BY a.anlage.anlagenName")
    List<SystemInfo> suchSystems(@Param("suchId") Long suchId, @Param("anlagenName") String anlagenName);

    Optional<SystemInfo> findByAnlageAnlagenId(Long anlageId);
}
