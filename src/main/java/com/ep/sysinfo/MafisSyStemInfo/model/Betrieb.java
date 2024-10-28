package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;

import com.ep.sysinfo.MafisSyStemInfo.model.SystemInfo;
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
 * Betrieb entity class
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Betrieb implements Serializable {

    @Serial
    private static final long serialVersionUID = 1991375623795012882L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer betriebNr;

    @Column
    private String betriebName;

    @Column
    private String beschreibung;

    @Column
    private boolean zentrale;

    @Column
    private String eigenstaendig;

    @Column
    private String zugBetrieb;

    @Column
    private boolean ustOrganschaft;

    @Column
    private boolean mutterBetrieb;

    @ManyToOne
    @JoinColumn(nullable = false, name = "system_id")
    private SystemInfo system;

}
