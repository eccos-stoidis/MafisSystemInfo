package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;

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
 * Schnittstelle entity class with Lombok annotations
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schnittstelle implements Serializable {

    @Serial
    private static final long serialVersionUID = 1991375623795012882L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer schnittstellenId;

    @Column
    private String bezeichnung;

    @Column
    private String typ;

    @Column
    private String unterTyp;

    @Column
    private String betrieb;

    @Column
    private Boolean aktiv;

    @Column(length = 1)
    private String mediumMitTyp;

    @Column
    private Integer mafisPort;

    @Column(length = 1)
    private String sslVeschluesselung;

    @Column
    private String encoding;

    @Column
    private String kommunikationstyp;

    @Column(length = 1)
    private String protokollierung;

    @Column(length = 1)
    private String httpHeader;

    @Column
    private Integer timeout;

    @Column
    private String endPoint;

    @Column
    private Integer kassenNr;

    @ManyToOne
    @JoinColumn(nullable = false, name = "system_id")
    private SystemInfo system;
}
