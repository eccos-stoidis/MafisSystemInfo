package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Computer entity class with Lombok annotations
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Computer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1130611481884158931L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long anlagenNr;

    @Column
    private String computerName;

    @Column
    private String hostname;

    @Column
    private String betriebsSystem;

    @Column
    private String betriebssystemVersion;

    @Column
    private String servicePack;

    @Column
    private String userHome;

    @Column
    private String javaVersion;

    @Column
    private String javaHome;

    @Column(name = "memory", columnDefinition = "TEXT")
    private String memory;

    @Transient
    private List<String> memoryListe;

    @Column(name = "prozessoren", columnDefinition = "TEXT")
    private String prozessoren;

    @Transient
    private List<String> prozessorenListe;

    @Column(name = "anzahlProcessors", columnDefinition = "TEXT")
    private String anzahlProcessors;

    @Transient
    private List<String> anzahlProcessorsListe;

    @Column(name = "drives", columnDefinition = "TEXT")
    private String drives;

    @Transient
    private List<String> drivesListe;

    @Column(name = "videoCards", columnDefinition = "TEXT")
    private String videoCards;

    @Transient
    private List<String> videoCardsListe;

    @Column(name = "physicalDiskInformation", columnDefinition = "TEXT")
    private String physicalDiskInformation;

    @Transient
    private List<String> physicalDiskInformationListe;

    @Column(name = "cdRomInformation", columnDefinition = "TEXT")
    private String cdRomInformation;

    @Transient
    private List<String> cdRomInformationListe;

    @Column
    private String anzahlPrinter;

    @Column(name = "printer", columnDefinition = "TEXT")
    private String printer;

    @Transient
    private List<String> printerListe;

    @Column
    private LocalDateTime lastModified;

    @Transient
    private String anlagenName;
}
