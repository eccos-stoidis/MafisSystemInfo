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
 * Kasse entity class with Lombok annotations
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kasse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1991375623795012882L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kasse_id;

    @Column
    private String bezeichnung;

    @Column
    private String typ;

    @Column
    private Integer kassenNr;

    @Column
    private Integer arbeitsplatzId;

    @Column
    private String eingang;

    @Column
    private String ausgang;

    @Column
    private Boolean hauptKasse;

    @Column
    private Boolean ausgangAktivierbar;

    @Column
    private String profitCenter;

    @Column
    private String ip;

    @Column
    private Boolean externVerkauf;

    @Column
    private Boolean internVerkauf;

    @ManyToOne
    @JoinColumn(nullable = false, name = "system_id")
    private  SystemInfo system;
}
