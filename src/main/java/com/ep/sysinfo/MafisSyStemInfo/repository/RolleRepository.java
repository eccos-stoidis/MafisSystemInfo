package com.ep.sysinfo.MafisSyStemInfo.repository;

import com.ep.sysinfo.MafisSyStemInfo.model.Rolle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource
@Repository
@Transactional
public interface RolleRepository extends JpaRepository<Rolle, Long> {

    @Transactional(readOnly=true)
    @Query("SELECT a FROM Rolle a  WHERE a.rolleName = :rolle_name ")
    Rolle findeRolleByName(@Param("rolle_name") String  rolle_name);
}
