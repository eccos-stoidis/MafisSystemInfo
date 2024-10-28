package com.ep.sysinfo.MafisSyStemInfo.repository;

import java.util.List;
import java.util.Optional;

import com.ep.sysinfo.MafisSyStemInfo.model.Anlage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author efstratios
 *
 */
@RepositoryRestResource
public interface AnlageRepository extends JpaRepository<Anlage, Long> {


    @Transactional(readOnly = true)
    @Query("SELECT a FROM Anlage a WHERE a.anlagenNr = :anlagenNr")
    Anlage existsAnlage(@Param("anlagenNr") Long anlagenNr);

    Optional<Anlage> findByAnlagenNr(Long anlagenNr);


    @Query("SELECT a " +
            "FROM Anlage a " +
            "WHERE (a.anlagenName LIKE CONCAT('%',:suchbegriff,'%') " +
            "OR CAST(a.anlagenNr AS string) LIKE CONCAT('%',:suchbegriff,'%') " +
            "OR a.mafisVersion LIKE CONCAT('%',:suchbegriff,'%') " +
            "OR a.ort LIKE CONCAT('%',:suchbegriff,'%')) " +
            "AND (a.status = :statusFilter OR :statusFilter IS NULL)")
    Page<Anlage> filterAnlagen(@Param("suchbegriff") String suchbegriff,  @Param("statusFilter") Boolean statusFilter, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Anlage a SET a.status= :status WHERE a.anlagenNr = :anlagen_nr  ")
    void updateStatusAnlage(@Param("anlagen_nr") Long anlagen_nr, @Param("status") Boolean status);

    @Transactional(readOnly = true)
    @Query("SELECT a FROM Anlage a WHERE a.status= :status order by a.anlagenName")
    List<Anlage> listeAnlage(@Param("status") Boolean status);

    @Transactional(readOnly = true)
    List<Anlage> findByStatus(Boolean status, Sort sort);
}
