package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FiskalDto class with Lombok annotations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiskalDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -4971816237361058074L;

    private Long anlagenNr;
    private String anlagenName;
    private String mafisVersion;
    private String typ;
    private Long fiskalCount;
    private Boolean registriert;
}
