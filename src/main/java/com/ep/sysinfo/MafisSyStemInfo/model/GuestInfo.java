package com.ep.sysinfo.MafisSyStemInfo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "system")  // Exclude system to avoid infinite recursion
public class GuestInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1991375623795012882L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private Long totalGuestsAdult;

    @Column
    private Long totalGuestsChild;

    @OneToOne
    @JoinColumn(name = "system_id", unique = true)
    private SystemInfo system;



}
