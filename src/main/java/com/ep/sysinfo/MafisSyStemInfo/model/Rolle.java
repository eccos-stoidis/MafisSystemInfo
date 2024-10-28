package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Rolle entity class with Lombok annotations
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "userRolle")  // Exclude userRolle to avoid circular references
public class Rolle implements Serializable {

    @Serial
    private static final long serialVersionUID = 4818182032550483166L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rolleId;

    @Column(nullable = false, unique = true)
    private String rolleName;

    @OneToMany(mappedBy = "rolle", fetch = FetchType.LAZY)
    private Set<BenutzerRolle> userRolle;
}
