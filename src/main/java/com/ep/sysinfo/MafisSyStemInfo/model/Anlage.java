package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;

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
 * Anlage entity class
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "system")  // Exclude system to avoid infinite recursion
public class Anlage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1991375623795012882L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long anlagenId;

    @Column(nullable = false, unique = true)
    private Long anlagenNr;

    @Column
    private String ort;

    @Column(nullable = false)
    private String anlagenName;

    @Column
    private String mafisVersion;

    @OneToOne
    @JoinColumn(name = "system_id", unique = true)
    private SystemInfo system;

    @Column
    private boolean status;

    @Column
    private Boolean testBetrieb;
}
