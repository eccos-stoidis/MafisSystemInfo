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
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * BenutzerRolle entity class with Lombok annotations
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"rolle", "benutzer"})  // Exclude rolle and benutzer to avoid circular references
public class BenutzerRolle implements Serializable {

    @Serial
    private static final long serialVersionUID = -3361718674272520569L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "rolleId")
    private Rolle rolle;

    @ManyToOne
    @JoinColumn(nullable = false, name = "userId")
    private Benutzer benutzer;

    @Column
    private LocalDateTime lastModified;
}
