package com.ep.sysinfo.MafisSyStemInfo.repository;

import com.ep.sysinfo.MafisSyStemInfo.model.Benutzer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RepositoryRestResource
@Repository
@Transactional
public interface BenutzerRepository  extends JpaRepository<Benutzer, Long> {



    Benutzer findByUsername(String  username);



    @Transactional(readOnly=true)
    @Query("SELECT a FROM Benutzer a  WHERE a.enabled = :enabled order by name")
    List<Benutzer> findeAktiveUser(@Param("enabled") String  enabled);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<Benutzer> findByEnabled(String enabled);
}
