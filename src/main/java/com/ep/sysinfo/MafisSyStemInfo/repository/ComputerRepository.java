
package com.ep.sysinfo.MafisSyStemInfo.repository;
import com.ep.sysinfo.MafisSyStemInfo.model.Computer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RepositoryRestResource
public interface ComputerRepository extends JpaRepository<Computer, Long> {


    /**
     * Finds a computer by the AnlagenNr.
     *
     * @param  anlagenNr  The AnlagenNr of the computer to find.
     * @return            The computer found based on the AnlagenNr.
     */
    Computer findByAnlagenNr(Long anlagenNr);
    @Transactional
    void deleteByAnlagenNr(Long anlagenNr);

    @Query("""
    SELECT c
    FROM Computer c
    WHERE c.anlagenNr IN :anlagenNrs
    """)
    List<Computer> findAllByAnlagenNrs(@Param("anlagenNrs") List<Long> anlagenNrs);

    @Query("SELECT c FROM Computer c WHERE " +
            "(c.anlagenNr = :suchBegriff) OR " +
            "(c.computerName IS NOT NULL AND LOWER(c.computerName) LIKE LOWER(CONCAT('%', :suchBegriff, '%'))) OR " +
            "(c.betriebsSystem IS NOT NULL AND LOWER(c.betriebsSystem) LIKE LOWER(CONCAT('%', :suchBegriff, '%'))) OR " +
            "(c.betriebssystemVersion IS NOT NULL AND LOWER(c.betriebssystemVersion) LIKE LOWER(CONCAT('%', :suchBegriff, '%')))")
    Page<Computer> searchBySuchBegriff(@Param("suchBegriff") String suchBegriff, Pageable pageable);


}


