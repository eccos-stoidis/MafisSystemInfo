package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Updates entity class with Lombok annotations
 * Hier ist die Einstellungen für automatische Updates von Mafis
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "system")
public class Updates implements Serializable {

    @Serial
    private static final long serialVersionUID = 1991375623795012882L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "system_id", unique = true)
    private SystemInfo system;

    @Column
    private boolean aktiv;

    @Column
    private boolean autoUpdate;

    @Column
    private String ftpServer;

    @Column
    private String proxy;

    @Column
    private LocalDateTime uhrzeitVon;

    @Column
    private LocalDateTime uhrzeitBis;

    @Column
    private boolean montag;

    @Column
    private boolean diesntag;

    @Column
    private boolean mittwoch;

    @Column
    private boolean donnerstag;

    @Column
    private boolean freitag;

    @Column
    private boolean samstag;

    @Column
    private boolean sonntag;
}
