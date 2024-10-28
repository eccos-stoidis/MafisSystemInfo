package com.ep.sysinfo.MafisSyStemInfo.repository;

import com.ep.sysinfo.MafisSyStemInfo.model.Benutzer;
import com.ep.sysinfo.MafisSyStemInfo.model.BenutzerRolle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@RepositoryRestResource
@Repository
@Transactional
public interface BenutzerRolleRepository extends JpaRepository<BenutzerRolle, Long> {

    @Transactional
    void deleteByBenutzer(Benutzer benutzer);
}
