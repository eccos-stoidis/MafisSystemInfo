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
 * ArbeitsPlatzFiskal entity class
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArbeitsPlatzFiskal implements Serializable {

    @Serial
    private static final long serialVersionUID = 1991375623795012882L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer arbeitsplatzId;

    @Column
    private String bezeichnung;

    @Column
    private String ip;

    @Column
    private Integer profitCenterId;

    @ManyToOne
    @JoinColumn(nullable = false, name = "fiskalRegId")
    private FiskalReg register;

    @Column
    private Integer kassenNr;

}
