package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * KennwortDTO class with Lombok annotations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KennwortDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4971816237361058074L;

    @Length(min = 4, message = "Das Kennwort muss mindestens 4 Ziffern haben!")
    private String altesKennwort;

    @Length(min = 4, message = "Das Kennwort muss mindestens 4 Ziffern haben!")
    private String neuesKennwort;

    @Length(min = 4, message = "Das Kennwort muss mindestens 4 Ziffern haben!")
    private String wiederholung;
}
