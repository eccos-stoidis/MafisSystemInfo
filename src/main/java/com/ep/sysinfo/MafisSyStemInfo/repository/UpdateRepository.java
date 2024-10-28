package com.ep.sysinfo.MafisSyStemInfo.repository;

import com.ep.sysinfo.MafisSyStemInfo.model.Updates;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UpdateRepository extends JpaRepository<Updates, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Updates s WHERE s.system.system_id = :system_id ")
    void deleteBySystemId(@Param("system_id") Long  system_id);
}
