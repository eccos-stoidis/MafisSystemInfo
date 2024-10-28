package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;
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
 * FiskalReg entity class with Lombok annotations
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiskalReg implements Serializable {

    @Serial
    private static final long serialVersionUID = 1991375623795012882L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bezeichnung;

    @Column
    private String kennung;

    @Column
    private String endPoint;

    @Column
    private String registerNr;

    @Column(nullable = false)
    private boolean registriert;

    @Column(nullable = false)
    private boolean benutzt;

    @ManyToOne
    @JoinColumn(nullable = false, name = "fiskal_id")
    private Fiskaldaten fiskal;

    @OneToMany(mappedBy = "register", fetch = FetchType.LAZY)
    private List<ArbeitsPlatzFiskal> arbeitsplatzListe;
}
