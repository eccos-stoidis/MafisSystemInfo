package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * SystemInfo entity class with Lombok annotations
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"anlage", "updates"})  // Exclude anlage to avoid infinite recursion
public class SystemInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 5624451986197649098L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long system_id;

    @OneToOne(mappedBy = "system", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Anlage anlage;

    @OneToMany(mappedBy = "system", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Collection<Modul> module;

    @OneToMany(mappedBy = "system", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Collection<Schnittstelle> schnittstellen;

    @OneToMany(mappedBy = "system", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Collection<Kasse> kassen;

    @OneToMany(mappedBy = "system", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Collection<Automat> automaten;

    @OneToOne(mappedBy = "system", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Updates updates;

    @OneToMany(mappedBy = "system", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Zutritt> zutritts;

    @OneToMany(mappedBy = "system", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT) // Load all `betriebe` in a single subselect query
    private Collection<Betrieb> betriebe;

    @OneToMany(mappedBy = "system", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Collection<Profitcenter> profitCenter;

    @OneToMany(mappedBy = "system", fetch = FetchType.LAZY)
    private Collection<Sektor> sektoren;

    @OneToMany(mappedBy = "system", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Collection<MedienArt> medienArten;

    @OneToMany(mappedBy = "system", fetch = FetchType.LAZY)
    private Collection<MedienTyp> medienTypen;

    @OneToMany(mappedBy = "system", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Collection<Fiskaldaten> fiskalService;

    @OneToOne(mappedBy = "system", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private GuestInfo guestsInfos;

    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @Transient
    private List<AutomatenTypDTO> automatenListe;

}
