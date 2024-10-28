//package com.ep.sysinfo.MafisSyStemInfo.repository;
//
//import com.ep.sysinfo.MafisSyStemInfo.model.Kundeninformation;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//
//@Repository
//public interface ExportRepository extends JpaRepository<Kundeninformation, Long> {
//
//    @Transactional(readOnly = true)
//    @Query("SELECT a.anlagenNr," +
//            "a.anlagenName, " +
//            "a.ort, " +
//            "a.system.lastModified, " +
//            "a.status," +
//            "a.mafisVersion," +
//            "a.testBetrieb," +
//            "a.system.systemUpdates.aktiv," +
//            "a.system.systemUpdates.autoUpdate," +
//            "c.computerName," +
//            "c.hostname," + // ist das wirklich die ip adresse?
//            "c.betriebsSystem," +
//            "c.betriebssystemVersion," +
//            "c.javaVersion," +
//            "c.javaHome," +
//            "f.typ, " +
//            "f.aktivAb " +
//            "FROM Anlage a " +
//            "LEFT JOIN Computer c ON c.anlagenNr = a.anlagenNr " +
//            "LEFT JOIN Fiskaldaten f ON f.system.system_id = a.system.system_id")
//    List<Kundeninformation> findAllForExport();
//
//    Optional<Kundeninformation> findKundenInformationOptionalKundenInformationAndAnlagenNr(Long anlagenNr);
//
//}
