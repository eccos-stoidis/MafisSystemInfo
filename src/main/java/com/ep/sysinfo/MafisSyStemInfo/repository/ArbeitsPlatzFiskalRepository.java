package com.ep.sysinfo.MafisSyStemInfo.repository;

import com.ep.sysinfo.MafisSyStemInfo.model.ArbeitsPlatzFiskal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource
public interface ArbeitsPlatzFiskalRepository extends JpaRepository<ArbeitsPlatzFiskal, Long> {


    /**
     * Deletes an entry in the ArbeitsPlatzFiskal table by register ID.
     *
     * @param  reg_id  the ID of the register to delete
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM ArbeitsPlatzFiskal a  WHERE a.register.id = :reg_id ")
    void deleteByRegId(@Param("reg_id") Long  reg_id);
}
