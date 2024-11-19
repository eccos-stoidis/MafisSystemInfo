package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Fiskaldaten entity class with Lombok annotations
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Fiskaldaten implements Serializable {

    @Serial
    private static final long serialVersionUID = 1991375623795012882L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fiskalId;

    @Column(name = "f_system_id", nullable = false)
    private String fiskalSystemId;

    @Column
    private String bezeichnung;

    @Column
    private String typ;

    @Column
    private LocalDateTime aktivAb;

    @Column
    private String format;

    @OneToMany(mappedBy = "fiskal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FiskalReg> regListe;

    @ManyToOne
    @JoinColumn(nullable = false, name = "system_id")
    private SystemInfo system;
}
