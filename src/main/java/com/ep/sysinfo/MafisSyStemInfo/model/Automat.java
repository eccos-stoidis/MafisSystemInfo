package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Automat entity class with Lombok annotations
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Automat implements Serializable {

    @Serial
    private static final long serialVersionUID = 1991375623795012882L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long automat_id;

    @Column
    private String bezeichnung;

    @Column
    @JsonAlias({"KASSENNR", "kassenNR","kassenNr"})
    private Integer kassenNr;

    @Column
    @JsonAlias({"ENGINEVERSION", "engineVersion", "engineversion"})
    private String engineVersion;

    @Column
    @JsonAlias({"FCCVERSION", "fccVersion","fccversion"})
    private String fccVersion;

    @Column
    private String typ;

    @Column
    private String unterTyp;

    @Column
    private Boolean aktivierbar;

    @Column
    private String treiber;

    @Column
    private String ip;

    @Column
    private String port;

    @Column
    private String profitcenter;

    @ManyToOne
    @JoinColumn(nullable = false, name = "system_id")
    private SystemInfo system;
}
