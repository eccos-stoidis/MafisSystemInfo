package com.ep.sysinfo.MafisSyStemInfo.repository;

import com.ep.sysinfo.MafisSyStemInfo.model.GuestInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface GuestInfoRepository extends JpaRepository<GuestInfo, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM GuestInfo g  WHERE g.system.system_id = :system_id ")
    void deleteBySystemId(@Param("system_id") Long  system_id);
}
