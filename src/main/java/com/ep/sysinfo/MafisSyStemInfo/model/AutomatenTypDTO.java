package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AutomatenTypDTO class with Lombok annotations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutomatenTypDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4971816237361058074L;

    private String type;

    private Long anzahl;
}
