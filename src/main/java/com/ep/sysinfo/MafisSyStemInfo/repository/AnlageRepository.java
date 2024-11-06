package com.ep.sysinfo.MafisSyStemInfo.repository;

import java.util.List;
import java.util.Optional;

import com.ep.sysinfo.MafisSyStemInfo.model.Anlage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @Query("SELECT DISTINCT a FROM Anlage a " +
            "JOIN FETCH a.system s " +
            "JOIN FETCH s.betriebe b " +
            "JOIN FETCH s.updates u " +  // Added JOIN FETCH for updates
            "WHERE b.betriebName = :betriebName AND a.status = true")
    List<Anlage> findAnlagenByBetriebName(@Param("betriebName") String betriebName);



    @Query("SELECT a FROM Anlage a " +
            "JOIN FETCH a.system s " +
            "JOIN FETCH s.profitCenter p " +
            "JOIN FETCH s.updates u " +
            "WHERE p.zugProfitcenter = :zugProfitcenter AND a.status = true")
    List<Anlage> findAnlageByZugProfitcenter(@Param("zugProfitcenter") String zugProfitcenter);

    @Query("SELECT a FROM Anlage a " +
            "JOIN FETCH a.system s " +
            "JOIN FETCH s.module m " +
            "JOIN FETCH s.updates u " +
            "WHERE m.modulTyp = :modulTyp AND a.status = true")
    List<Anlage> findAnlageByModulTyp(@Param("modulTyp") String modulTyp);

    @Query("SELECT a FROM Anlage a " +
            "JOIN FETCH a.system s " +
            "JOIN FETCH s.kassen k " +
            "JOIN FETCH s.updates u " +
            "WHERE k.typ IN :typ AND a.status = true")
    List<Anlage> findAnlagenByKasseTypes(@Param("typ") List<String> kasseTypes);


    @Query("SELECT a FROM Anlage a " +
            "JOIN FETCH a.system s " +
            "JOIN FETCH s.schnittstellen sc " +  // assuming Schnittstellen is a mapped entity
            "JOIN FETCH s.updates u " +
            "WHERE sc.typ = :typ AND a.status = true")
    List<Anlage> findByTyp(@Param("typ") String typ);

    @Query("SELECT a FROM Anlage a " +
            "JOIN FETCH a.system s " +
            "JOIN FETCH s.schnittstellen sc " +
            "JOIN FETCH s.updates u " +
            "WHERE sc.unterTyp = :untertyp AND a.status = true")
    List<Anlage> findByUntertyp(@Param("untertyp") String untertyp);

    @Query("SELECT a FROM Anlage a " +
            "JOIN FETCH a.system s " +
            "JOIN FETCH s.schnittstellen sc " +
            "JOIN FETCH s.updates u " +
            "WHERE sc.typ = :typ AND sc.unterTyp = :untertyp AND a.status = true")
    List<Anlage> findByTypAndUntertyp(@Param("typ") String typ, @Param("untertyp") String untertyp);

    @Query("SELECT a FROM Anlage a " +
            "JOIN FETCH a.system s " +
            "JOIN FETCH s.automaten auto " +
            "JOIN FETCH s.updates u " +
            "WHERE (:engineVersion IS NULL OR auto.engineVersion = :engineVersion) " +
            "AND (:fccVersion IS NULL OR auto.fccVersion = :fccVersion) " +
            "AND (:typ IS NULL OR auto.typ = :typ) " +
            "AND (:unterTyp IS NULL OR auto.unterTyp = :unterTyp) " +
            "AND a.status = true")
    List<Anlage> findAnlageByAutomatenParams(
            @Param("engineVersion") String engineVersion,
            @Param("fccVersion") String fccVersion,
            @Param("typ") String typ,
            @Param("unterTyp") String unterTyp);

    @Query("SELECT a FROM Anlage a " +
            "JOIN FETCH a.system s " +
            "JOIN FETCH s.zutritts z " +
            "JOIN FETCH s.updates u " +
            "WHERE (:vonSektor IS NULL OR z.vonSektor = :vonSektor) " +
            "AND (:nachSektor IS NULL OR z.nachSektor = :nachSektor) " +
            "AND a.status = true")
    List<Anlage> findAnlageByZutrittsParams(
            @Param("vonSektor") String vonSektor,
            @Param("nachSektor") String nachSektor);



    @Query("SELECT a FROM Anlage a " +
            "JOIN FETCH a.system s " +
            "JOIN FETCH s.fiskalService f " +
            "JOIN FETCH s.updates u " +
            "WHERE f.typ = 'efsta' AND a.status = true")
    List<Anlage> findAnlageByFiskaldatenEfsta();

    @Query("SELECT a FROM Anlage a " +
            "JOIN FETCH a.system s " +
            "JOIN FETCH s.fiskalService f " +
            "JOIN FETCH s.updates u " +
            "WHERE f.typ = 'fiskaltrust' AND a.status = true")
    List<Anlage> findAnlageByFiskaldatenFiskaltrust();

    @Query("SELECT a FROM Anlage a " +
            "JOIN FETCH a.system s " +
            "JOIN FETCH s.medienArten m " +
            "JOIN FETCH s.updates u " +
            "WHERE (:typ IS NULL OR m.typNr IN :typ) " +  // Handle null or empty list with a conditional// Add missing space
            "AND a.status = true")
    List<Anlage> findAnlageByMedienarten(@Param("typ") List<Integer> typ);
}
