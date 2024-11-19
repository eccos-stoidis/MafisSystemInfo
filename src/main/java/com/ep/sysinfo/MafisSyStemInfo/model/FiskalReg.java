package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * FiskalReg entity class with Lombok annotations
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

    @OneToMany(mappedBy = "register", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ArbeitsPlatzFiskal> arbeistplatzListe;
}
