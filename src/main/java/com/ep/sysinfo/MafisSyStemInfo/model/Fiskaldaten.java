package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Fiskaldaten entity class with Lombok annotations
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fiskaldaten implements Serializable {

    @Serial
    private static final long serialVersionUID = 1991375623795012882L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fiskalId;

    @Column
    private String fSystemId;

    @Column
    private String bezeichnung;

    @Column
    private String typ;

    @Column
    private LocalDateTime aktivAb;

    @Column
    private String format;

    @OneToMany(mappedBy = "fiskal", fetch = FetchType.LAZY)
    private List<FiskalReg> regListe;

    @ManyToOne
    @JoinColumn(nullable = false, name = "system_id")
    private SystemInfo system;
}
