package com.ep.sysinfo.MafisSyStemInfo.repository;

import com.ep.sysinfo.MafisSyStemInfo.model.FiskalReg;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RepositoryRestResource
public interface FiskalRegRepository extends JpaRepository<FiskalReg, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM FiskalReg f WHERE f.fiskal.fiskalId = :fiskal_id")
    void deleteBySystemId(@Param("fiskal_id") Long fiskalId);

    @EntityGraph(attributePaths = {
            "fiskal",
            "fiskal.system",
            "fiskal.system.anlage",
            "fiskal.system.updates"
    })
    List<FiskalReg> findAll();


}
